package com.ivanmagda.minimalautomedia;

import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.media.MediaBrowserService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Media browser service workflow:
 * <p>
 * 1. When your app's audio services are requested by a user through a connected Auto device,
 * the dashboard system contacts your app's media browser service.
 * In your implementation of the onCreate() method, you must create and register a MediaSession
 * object and its callback object.
 * 2. The Auto device calls the browser service's onGetRoot() method to get the top node of your
 * content hierarchy. The node retrieved by this call is not used as a menu item, it is only used
 * to retrieve its child nodes, which are subsequently displayed as the top menu items.
 * 3. Auto invokes the onLoadChildren() method to get the children of the root node, and uses this
 * information to present a menu to the user.
 * 4. If the user selects a submenu, Auto invokes onLoadChildren() again to retrieve the child
 * nodes of the selected menu item.
 * 5. If the user begins playback, Auto invokes the appropriate media session callback method to
 * perform that action. For more information, see the section about how to Implement Playback Controls.
 */
public class TestMusicBrowserService extends MediaBrowserService {

    private static final String TAG = "TestMusicBrowserService";

    private MediaSession mMediaSession;
    private List<MediaMetadata> mMusic;
    private MediaPlayer mMediaPlayer;
    private MediaMetadata mCurrentTrack;

    @Override
    public void onCreate() {
        super.onCreate();

        final String songUrl = "http://www.stephaniequinn.com/Music/Allegro%20from%20Duet%20in%20C%20Major.mp3";

        // Creates entries for two songs.
        mMusic = new ArrayList<>();
        mMusic.add(new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, songUrl)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "Music 1")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artists 1")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 60000)
                .build()
        );
        mMusic.add(new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, songUrl)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "Music 2")
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "Artists 2")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 30000)
                .build()
        );

        // Responsible for playing back music.
        mMediaPlayer = new MediaPlayer();

        mMediaSession = new MediaSession(this, TAG);
        // Callbacks to handle events from the user (play, pause, search)
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                for (MediaMetadata item : mMusic) {
                    final String itemMediaId = item.getDescription().getMediaId();
                    if (!TextUtils.isEmpty(itemMediaId) && itemMediaId.equals(mediaId)) {
                        mCurrentTrack = item;
                        break;
                    }
                }
                handlePlay();
            }

            @Override
            public void onPlay() {
                if (mCurrentTrack == null) {
                    // No current song selected, so pick the first one and start playing it.
                    mCurrentTrack = mMusic.get(0);
                    handlePlay();
                } else {
                    // Current song is ready, but paused, so start playing the music.
                    mMediaPlayer.start();
                    // Update the UI to show we are playing.
                    mMediaSession.setPlaybackState(buildState(PlaybackState.STATE_PLAYING));
                }
            }

            @Override
            public void onPause() {
                mMediaPlayer.pause();
                mMediaSession.setPlaybackState(buildState(PlaybackState.STATE_PAUSED));
            }
        });

        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setActive(true);

        setSessionToken(mMediaSession.getSessionToken());
    }

    private PlaybackState buildState(int state) {
        return new PlaybackState.Builder().setActions(
                PlaybackState.ACTION_PLAY | PlaybackState.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackState.ACTION_SKIP_TO_NEXT
                        | PlaybackState.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackState.ACTION_PLAY_PAUSE)
                .setState(state, mMediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime())
                .build();
    }

    // Helper method to start playing a track
    private void handlePlay() {
        mMediaSession.setPlaybackState(buildState(PlaybackState.STATE_PLAYING));
        mMediaSession.setMetadata(mCurrentTrack);

        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(TestMusicBrowserService.this,
                    Uri.parse(mCurrentTrack.getDescription().getMediaId()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set a callback for when the music is ready to be palyed.
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        // Tell the player to start downloading the track.
        mMediaPlayer.prepareAsync();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        // Return a root node since we want to allow the client to browse our collection.
        return new BrowserRoot("ROOT", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowser.MediaItem>> result) {
        List<MediaBrowser.MediaItem> list = new ArrayList<>(mMusic.size());
        for (MediaMetadata metadata : mMusic) {
            list.add(new MediaBrowser.MediaItem(metadata.getDescription(),
                    MediaBrowser.MediaItem.FLAG_PLAYABLE));
        }
        result.sendResult(list);
    }

}
