/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.request;

import android.util.Log;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserProfileGender;

public class RequestUserProfileSetGender {

    public void setUserProfileGender(ProtoGlobal.Gender gender) {

        ProtoUserProfileGender.UserProfileSetGender.Builder userProfileGender =
                ProtoUserProfileGender.UserProfileSetGender.newBuilder();
        userProfileGender.setGender(gender);

        Log.i("XXX", "RequestUserProfileSetGender gender : " + gender);

        RequestWrapper requestWrapper = new RequestWrapper(104, userProfileGender);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
