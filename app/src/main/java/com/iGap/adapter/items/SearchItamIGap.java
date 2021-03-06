/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.adapter.items;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperCalander;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.CustomTextViewMedium;
import com.iGap.proto.ProtoClientSearchUsername;
import com.iGap.proto.ProtoGlobal;
import com.mikepenz.fastadapter.items.AbstractItem;
import io.github.meness.emoji.EmojiTextView;
import java.util.List;

public class SearchItamIGap extends AbstractItem<SearchItamIGap, SearchItamIGap.ViewHolder> {
    ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item;
    private Typeface typeFaceIcon;

    public SearchItamIGap setItem(ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item) {
        this.item = item;
        return this;
    }

    public ProtoClientSearchUsername.ClientSearchUsernameResponse.Result getItem() {
        return item;
    }

    @Override
    public int getType() {
        return R.id.sfsl_imv_contact_avatar;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.search_fragment_sub_layout;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);


        holder.txtIcon.setVisibility(View.GONE);

        if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {

            HelperAvatar.getAvatar(item.getUser().getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long roomId) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.avatar);
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            holder.name.setText(item.getUser().getDisplayName());
            holder.lastSeen.setText(item.getUser().getUsername());
        } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {

            HelperAvatar.getAvatar(item.getRoom().getId(), HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
                @Override
                public void onAvatarGet(final String avatarPath, long roomId) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), holder.avatar);
                        }
                    });
                }

                @Override
                public void onShowInitials(final String initials, final String color) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.avatar.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) holder.avatar.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                        }
                    });
                }
            });

            holder.name.setText(item.getRoom().getTitle());

            if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                holder.lastSeen.setText(item.getRoom().getChannelRoomExtra().getPublicExtra().getUsername());
            } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                holder.lastSeen.setText(item.getRoom().getGroupRoomExtra().getPublicExtra().getUsername());
            }


            if (item.getRoom().getType() == ProtoGlobal.Room.Type.GROUP) {
                typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.md_users_social_symbol));
            } else if (item.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) {
                typeFaceIcon = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap_font.ttf");
                holder.txtIcon.setTypeface(typeFaceIcon);
                holder.txtIcon.setVisibility(View.VISIBLE);
                holder.txtIcon.setText(G.context.getString(R.string.md_channel_icon));
            }
        }

        holder.txtTime.setText("");

        if (HelperCalander.isLanguagePersian) {
            holder.name.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.name.getText().toString()));
            holder.lastSeen.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.lastSeen.getText().toString()));
            holder.txtTime.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtTime.getText().toString()));
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected CircleImageView avatar;
        protected CustomTextViewMedium name;
        protected TextView txtIcon;
        protected EmojiTextView lastSeen;
        protected TextView txtTime;

        public ViewHolder(View view) {
            super(view);

            avatar = (CircleImageView) view.findViewById(R.id.sfsl_imv_contact_avatar);
            name = (CustomTextViewMedium) view.findViewById(R.id.sfsl_txt_contact_name);
            lastSeen = (EmojiTextView) view.findViewById(R.id.sfsl_txt_contact_lastseen);
            txtIcon = (TextView) view.findViewById(R.id.sfsl_txt_icon);
            txtTime = (TextView) view.findViewById(R.id.sfsl_txt_time);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}


