/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.module;

import android.support.annotation.IntDef;


@IntDef({IntentRequests.REQ_CROP, IntentRequests.REQ_CAMERA, IntentRequests.REQ_GALLERY}) public @interface IntentRequests {
    int REQ_CROP = 100;
    int REQ_CAMERA = 101;
    int REQ_GALLERY = 102;
}