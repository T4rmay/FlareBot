package com.bwfcwalshy.flarebot.music.extractors;

import com.arsenarsen.lavaplayerbridge.player.Player;
import com.arsenarsen.lavaplayerbridge.player.Playlist;
import com.arsenarsen.lavaplayerbridge.player.Track;
import com.bwfcwalshy.flarebot.MessageUtils;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YouTubeExtractor implements Extractor {
    public static final String YOUTUBE_URL = "https://www.youtube.com";
    public static final String PLAYLIST_URL = "https://www.youtube.com/playlist?list=";
    public static final String WATCH_URL = "https://www.youtube.com/watch?v=";
    public static final String ANY_YT_URL = "(?:https?://)?(?:(?:(?:(?:(?:www\\.)|(?:m\\.))?(?:youtube\\.com))/(?:(?:watch\\?v=([^?&\\n]+)(?:&(?:[^?&\\n]+=(?:[^?&\\n]+)))*)|(?:playlist\\?list=([^&?]+))(?:&[^&]*=[^&]+)?))|(?:youtu\\.be/(.*)))";

    @Override
    public Class<? extends AudioSourceManager> getSourceManagerClass() {
        return YoutubeAudioSourceManager.class;
    }

    @Override
    public void process(String input, Player player, IMessage message, IUser user) throws Exception {
        AudioItem item;
        try {
            item = player.resolve(input);
            if (item == null) {
                MessageUtils.editMessage(MessageUtils.getEmbed(user)
                        .withDesc("Could not get that video/playlist! Make sure the URL is correct!"), message);
                return;
            }
        } catch (RuntimeException e) {
            MessageUtils.editMessage(MessageUtils.getEmbed(user)
                    .withDesc("Could not get that video/playlist!")
                    .appendField("YouTube said: ", e.getMessage(), true), message);
            return;
        }
        List<AudioTrack> audioTracks = new ArrayList<>();
        String name;
        if (item instanceof AudioPlaylist) {
            AudioPlaylist audioPlaylist = (AudioPlaylist) item;
            audioTracks.addAll(audioPlaylist.getTracks());
            name = audioPlaylist.getName();
        } else {
            AudioTrack track = (AudioTrack) item;
            if (track.getInfo().length == 0 || track.getInfo().isStream) {
                EmbedBuilder builder = MessageUtils.getEmbed(user).withDesc("Cannot queue a livestream!");
                MessageUtils.editMessage("", builder, message);
                return;
            }
            audioTracks.add(track);
            name = track.getInfo().title;
        }
        if (name != null) {
            List<Track> tracks = audioTracks.stream().map(Track::new).map(track -> {
                track.getMeta().put("requester", user.getID());
                return track;
            }).collect(Collectors.toList());
            if(tracks.size() > 1) { // Double `if` https://giphy.com/gifs/ng1xAzwIkDgfm
                Playlist p = new Playlist(tracks);
                player.queue(p);
            } else {
                player.queue(tracks.get(0));
            }
            EmbedBuilder builder = MessageUtils.getEmbed(user);
            builder.withDesc(String.format("%s added the %s [`%s`](%s)", user, audioTracks.size() == 1 ? "song" : "playlist",
                    name, input));
            if (audioTracks.size() > 1)
                builder.appendField("Song count:", String.valueOf(audioTracks.size()), true);
            MessageUtils.editMessage("", builder, message);
        }
    }

    @Override
    public boolean valid(String input) {
        return input.matches(ANY_YT_URL);
    }
}
