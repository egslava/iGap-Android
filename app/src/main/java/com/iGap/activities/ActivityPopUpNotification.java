/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.StringRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IPopUpListener;
import com.iGap.interfaces.OnVoiceRecord;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.ChatSendMessageUtil;
import com.iGap.module.LastSeenTimeUtil;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.TimeUtils;
import com.iGap.module.UploadService;
import com.iGap.module.VoiceRecord;
import com.iGap.module.enums.StructPopUp;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import io.github.meness.emoji.EmojiEditText;
import io.github.meness.emoji.emoji.Emoji;
import io.github.meness.emoji.listeners.OnEmojiBackspaceClickListener;
import io.github.meness.emoji.listeners.OnEmojiClickedListener;
import io.github.meness.emoji.listeners.OnEmojiPopupDismissListener;
import io.github.meness.emoji.listeners.OnEmojiPopupShownListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardCloseListener;
import io.github.meness.emoji.listeners.OnSoftKeyboardOpenListener;
import io.realm.Realm;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.iGap.G.context;

public class ActivityPopUpNotification extends ActivityEnhanced {

    public static boolean isPopUpVisible = false;

    public static IPopUpListener popUpListener;

    public static boolean isGoingToChatFromPopUp = false;
    public static String ARGUMENTLIST = "argument_list";

    //////////////////////////////////////////   appbar component
    ViewPager viewPager;
    ArrayList<StructPopUp> mList;
    private TextView txtName;
    private TextView txtLastSeen;

    //////////////////////////////////////////
    private ImageView imvUserPicture;
    private Button btnMessageCounter;
    private View viewAttachFile;
    private View viewMicRecorder;

    //////////////////////////////////////////    attach layout
    private MaterialDesignTextView btnSmileButton;
    private EmojiEditText edtChat;
    private MaterialDesignTextView btnMic;

    //////////////////////////////////////////
    private MaterialDesignTextView btnSend;
    private VoiceRecord voiceRecord;
    private boolean sendByEnter = false;
    private AdapterViewPagerClass mAdapter;
    private int listSize = 0;
    private InitComponnet initComponnet;

    private String initialize;
    private String color;
    private long chatPeerId;

    /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        isPopUpVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPopUpVisible = false;

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        KeyguardManager.KeyguardLock lock = ((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        PowerManager.WakeLock wake = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        lock.disableKeyguard();
        wake.acquire();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON, WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mList = (ArrayList<StructPopUp>) getIntent().getExtras().getSerializable(ARGUMENTLIST);

        if (mList == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_popup_notification);

        initComponnet = new InitComponnet();

    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        btnSmileButton.setText(drawableResourceId);
    }

    private io.github.meness.emoji.EmojiPopup emojiPopup;

    private void setUpEmojiPopup() {
        emojiPopup = io.github.meness.emoji.EmojiPopup.Builder.fromRootView(findViewById(R.id.ac_ll_parent)).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {

            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {

            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                changeEmojiButtonImageResource(R.string.md_black_keyboard_with_white_keys);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {

            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                changeEmojiButtonImageResource(R.string.md_emoticon_with_happy_face);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(edtChat);
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void setImageAndTextAppBar(int position) {
        if (mList.isEmpty() || position > mList.size() - 1 || position < 0) {
            return;
        }

        Realm realm = Realm.getDefaultInstance();

        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mList.get(position).getRoomId()).findFirst();

        if (realmRoom != null) { // room exist

            mList.get(position).setRoomType(realmRoom.getType().toString());

            initialize = realmRoom.getInitials();
            color = realmRoom.getColor();

            txtName.setText(realmRoom.getTitle());
            setLastSeen(realmRoom, realm);
            setAvatar(realm);
        }

        realm.close();
    }

    private void setLastSeen(RealmRoom realmRoom, Realm realm) {
        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
        if (realmRoom.getChatRoom() != null) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, realmChatRoom.getPeerId()).findFirst();
            if (realmRegisteredInfo != null) {
                if (realmRegisteredInfo.getStatus().equals(ProtoGlobal.RegisteredUser.Status.EXACTLY.toString())) {
                    txtLastSeen.setText(LastSeenTimeUtil.computeTime(realmRegisteredInfo.getId(), realmRegisteredInfo.getLastSeen(), false));
                } else {
                    txtLastSeen.setText(realmRegisteredInfo.getStatus());
                }
            }
        }
    }

    private void setAvatar(Realm realm) {

        String avatarPath = null;

        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, chatPeerId).findFirst();
        if (realmRegisteredInfo != null && realmRegisteredInfo.getAvatars() != null && realmRegisteredInfo.getLastAvatar() != null) {

            String mainFilePath = realmRegisteredInfo.getLastAvatar().getFile().getLocalFilePath();

            if (mainFilePath != null && new File(mainFilePath).exists()) { // if main image is exist showing that
                avatarPath = mainFilePath;
            } else {
                avatarPath = realmRegisteredInfo.getLastAvatar().getFile().getLocalThumbnailPath();
            }
        }

        //Set Avatar For Chat,Group,Channel
        if (avatarPath != null) {
            File imgFile = new File(avatarPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imvUserPicture.setImageBitmap(myBitmap);
            } else {
                if (realmRegisteredInfo != null && realmRegisteredInfo.getLastAvatar() != null && realmRegisteredInfo.getLastAvatar().getFile() != null) {
                    // onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
                }
                imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));
            }
        } else {
            if (realmRegisteredInfo != null && realmRegisteredInfo.getLastAvatar() != null && realmRegisteredInfo.getLastAvatar().getFile() != null) {
                //  onRequestDownloadAvatar(realmRegisteredInfo.getLastAvatar().getFile());
            }
            imvUserPicture.setImageBitmap(com.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) imvUserPicture.getContext().getResources().getDimension(R.dimen.dp60), initialize, color));
        }
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (voiceRecord != null) {
            voiceRecord.dispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    /////////////////////////////////////////////////////////////////////////////////////////

    private void sendMessage(final String message, final long mRoomId, ProtoGlobal.Room.Type chatType) {

        final Realm realm = Realm.getDefaultInstance();
        final long senderId = realm.where(RealmUserInfo.class).findFirst().getUserId();

        final String identity = Long.toString(System.currentTimeMillis());

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, Long.parseLong(identity));

                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                roomMessage.setRoomId(mRoomId);
                roomMessage.setMessage(message);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setUserId(senderId);
                roomMessage.setCreateTime(TimeUtils.currentLocalTime());
            }
        });

        realm.close();

        new ChatSendMessageUtil().newBuilder(chatType, ProtoGlobal.RoomMessageType.TEXT, mRoomId).message(message).sendMessage(identity);
    }

    private void goToChatActivity() {

        Intent intent = new Intent(context, ActivityChat.class);
        intent.putExtra("RoomId", mList.get(viewPager.getCurrentItem()).getRoomId());
        startActivity(intent);

        isGoingToChatFromPopUp = true;

        finish();


    }

    private class InitComponnet {

        public InitComponnet() {

            initMethode();
            initAppbar();
            initViewPager();
            initLayoutAttach();

            setUpEmojiPopup();
        }

        private void initMethode() {

            popUpListener = new IPopUpListener() {
                @Override public void onMessageRecive(final ArrayList<StructPopUp> list) {

                    viewPager.post(new Runnable() {
                        @Override public void run() {

                            mList.clear();
                            mList = (ArrayList<StructPopUp>) list.clone();

                            viewPager.setAdapter(mAdapter);
                            btnMessageCounter.setText(1 + "/" + mList.size());

                            setImageAndTextAppBar(viewPager.getCurrentItem());
                            listSize = mList.size();
                        }
                    });




                }
            };


            viewAttachFile = findViewById(R.id.apn_layout_attach_file);

            viewMicRecorder = findViewById(R.id.apn_layout_mic_recorde);

            voiceRecord = new VoiceRecord(ActivityPopUpNotification.this, viewMicRecorder, viewAttachFile, new OnVoiceRecord() {
                @Override
                public void onVoiceRecordDone(String savedPath) {

                    Intent uploadService = new Intent(ActivityPopUpNotification.this, UploadService.class);
                    uploadService.putExtra("Path", savedPath);
                    uploadService.putExtra("Roomid", mList.get(viewPager.getCurrentItem()).getRoomId());
                    startService(uploadService);

                    // sendVoice(savedPath, unreadList.get(viewPager.getCurrentItem()).getRoomId());

                    finish();
                }

                @Override
                public void onVoiceRecordCancel() {

                }
            });

            // get sendByEnter action from setting value
            SharedPreferences sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            int checkedSendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
            sendByEnter = checkedSendByEnter == 1;
        }

        private void initAppbar() {

            RippleView rippleBackButton = (RippleView) findViewById(R.id.apn_ripple_back_Button);

            rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    finish();

                }
            });

            findViewById(R.id.apn_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

            txtName = (TextView) findViewById(R.id.apn_txt_name);
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });

            txtLastSeen = (TextView) findViewById(R.id.apn_txt_last_seen);

            imvUserPicture = (ImageView) findViewById(R.id.apn_imv_user_picture);
            imvUserPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });

            btnMessageCounter = (Button) findViewById(R.id.apn_btn_message_counter);
            btnMessageCounter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        private void initViewPager() {

            viewPager = (ViewPager) findViewById(R.id.apn_view_pager);
            mAdapter = new AdapterViewPagerClass();
            viewPager.setAdapter(mAdapter);
            listSize = mList.size();

            btnMessageCounter.setText(1 + "/" + listSize);

            setImageAndTextAppBar(viewPager.getCurrentItem());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    btnMessageCounter.setText(position + 1 + "/" + listSize);

                    setImageAndTextAppBar(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


        }

        private void initLayoutAttach() {

            btnSmileButton = (MaterialDesignTextView) findViewById(R.id.apn_btn_smile_button);
            btnSmileButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    emojiPopup.toggle();
                }
            });

            edtChat = (EmojiEditText) findViewById(R.id.apn_edt_chat);

            edtChat.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                    // if in the seeting page send by enter is on message send by enter key
                    if (text.toString().endsWith(System.getProperty("line.separator"))) {
                        if (sendByEnter) btnSend.performClick();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    if (edtChat.getText().length() > 0) {
                        btnMic.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.GONE);
                            }
                        }).start();
                        btnSend.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.VISIBLE);
                            }
                        }).start();
                    } else {
                        btnMic.animate().alpha(1F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMic.setVisibility(View.VISIBLE);
                            }
                        }).start();
                        btnSend.animate().alpha(0F).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnSend.setVisibility(View.GONE);
                            }
                        }).start();
                    }

                    // android emoji one doesn't support common space unicode
                    // to support space character, a new unicode will be replaced.
                    if (editable.toString().contains("\u0020")) {
                        Editable ab = new SpannableStringBuilder(editable.toString().replace("\u0020", "\u2000"));
                        editable.replace(0, editable.length(), ab);
                    }
                }
            });

            btnMic = (MaterialDesignTextView) findViewById(R.id.apn_btn_mic);
            btnMic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    voiceRecord.setItemTag("ivVoice");
                    viewAttachFile.setVisibility(View.GONE);
                    viewMicRecorder.setVisibility(View.VISIBLE);
                    voiceRecord.startVoiceRecord();

                    return true;
                }
            });

            btnSend = (MaterialDesignTextView) findViewById(R.id.apn_btn_send);
            btnSend.setTextColor(Color.parseColor(G.attachmentColor));

            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = viewPager.getCurrentItem();

                    sendMessage(edtChat.getText().toString(), mList.get(position).getRoomId(), ProtoGlobal.Room.Type.valueOf(mList.get(position).getRoomType()));

                    edtChat.setText("");

                    finish();
                }
            });
        }
    }

    private class AdapterViewPagerClass extends PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(View container, final int position) {

            LayoutInflater inflater = LayoutInflater.from(ActivityPopUpNotification.this);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.sub_layout_activity_popup_notification, (ViewGroup) container, false);

            TextView txtMessage = (TextView) layout.findViewById(R.id.slapn_txt_message);
            txtMessage.setText(mList.get(position).getMessage());

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChatActivity();
                }
            });


            ((ViewGroup) container).addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public static String getTextOfMessageType(ProtoGlobal.RoomMessageType messageType) {

        switch (messageType) {
            case VOICE:
                return G.context.getString(R.string.voice_message);
            case VIDEO:
                return G.context.getString(R.string.video_message);
            case FILE:
                return G.context.getString(R.string.file_message);
            case AUDIO:
                return G.context.getString(R.string.audio_message);
            case IMAGE:
                return G.context.getString(R.string.image_message);
            case CONTACT:
                return G.context.getString(R.string.contact_message);
            case GIF:
                return G.context.getString(R.string.gif_message);
            case LOCATION:
                return G.context.getString(R.string.location_message);
        }

        return "";
    }

    /**
     * Checks if the application is being sent in the background (i.e behind
     * another application's Activity).
     *
     * @param context the context
     * @return <code>true</code> if another application will be above this one.
     */
    public static boolean isApplicationSentToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
