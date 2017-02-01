package com.bwfcwalshy.flarebot.commands.general;

import com.bwfcwalshy.flarebot.MessageUtils;
import com.bwfcwalshy.flarebot.commands.Command;
import com.bwfcwalshy.flarebot.commands.CommandType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;

public class JoinCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        if (!sender.getConnectedVoiceChannels().isEmpty()) {
            IVoiceChannel voiceChannel = sender.getConnectedVoiceChannels().get(0);
            try {
                voiceChannel.join();
            } catch (MissingPermissionsException e) {
                MessageUtils.sendMessage(sender.mention() + " I cannot join that voice channel!", channel);
            }
        }
    }

    @Override
    public String getCommand() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Tell me to join your voice channel.";
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
