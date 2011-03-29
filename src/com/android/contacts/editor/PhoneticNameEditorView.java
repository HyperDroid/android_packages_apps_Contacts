/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.contacts.editor;

import com.android.contacts.model.DataKind;
import com.android.contacts.model.EntityDelta;
import com.android.contacts.model.EntityDelta.ValuesDelta;

import android.content.Context;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * A dedicated editor for phonetic name. It is similar to {@link StructuredNameEditorView}.
 */
public class PhoneticNameEditorView extends TextFieldsEditorView {

    private static class PhoneticValuesDelta extends ValuesDelta {
        private ValuesDelta mValues;
        private String mPhoneticName;

        public PhoneticValuesDelta(ValuesDelta values) {
            mValues = values;
            buildPhoneticName();
        }

        @Override
        public void put(String key, String value) {
            if (key.equals(DataKind.PSEUDO_COLUMN_PHONETIC_NAME)) {
                mPhoneticName = value;
                parsePhoneticName(value);
            } else {
                mValues.put(key, value);
                buildPhoneticName();
            }
        }

        @Override
        public String getAsString(String key) {
            if (key.equals(DataKind.PSEUDO_COLUMN_PHONETIC_NAME)) {
                return mPhoneticName;
            } else {
                return mValues.getAsString(key);
            }
        }

        private void parsePhoneticName(String value) {
            String family = null;
            String middle = null;
            String given = null;

            if (!TextUtils.isEmpty(value)) {
                String[] strings = value.split(" ", 3);
                switch (strings.length) {
                    case 1:
                        family = strings[0];
                        break;
                    case 2:
                        family = strings[0];
                        given = strings[1];
                        break;
                    case 3:
                        family = strings[0];
                        middle = strings[1];
                        given = strings[2];
                        break;
                }
            }

            mValues.put(StructuredName.PHONETIC_FAMILY_NAME, family);
            mValues.put(StructuredName.PHONETIC_MIDDLE_NAME, middle);
            mValues.put(StructuredName.PHONETIC_GIVEN_NAME, given);
        }

        private void buildPhoneticName() {
            String family = mValues.getAsString(StructuredName.PHONETIC_FAMILY_NAME);
            String middle = mValues.getAsString(StructuredName.PHONETIC_MIDDLE_NAME);
            String given = mValues.getAsString(StructuredName.PHONETIC_GIVEN_NAME);

            if (!TextUtils.isEmpty(family) || !TextUtils.isEmpty(middle)
                    || !TextUtils.isEmpty(given)) {
                StringBuilder sb = new StringBuilder();
                if (!TextUtils.isEmpty(family)) {
                    sb.append(family.trim()).append(' ');
                }
                if (!TextUtils.isEmpty(middle)) {
                    sb.append(middle.trim()).append(' ');
                }
                if (!TextUtils.isEmpty(given)) {
                    sb.append(given.trim()).append(' ');
                }
                sb.setLength(sb.length() - 1);  // Yank the last space
                mPhoneticName = sb.toString();
            } else {
                mPhoneticName = null;
            }
        }

        @Override
        public Long getId() {
            return mValues.getId();
        }

        @Override
        public boolean isVisible() {
            return mValues.isVisible();
        }
    }

    public PhoneticNameEditorView(Context context) {
        super(context);
    }

    public PhoneticNameEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhoneticNameEditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setValues(DataKind kind, ValuesDelta entry, EntityDelta state, boolean readOnly,
            ViewIdGenerator vig) {
        if (!(entry instanceof PhoneticValuesDelta)) {
            entry = new PhoneticValuesDelta(entry);
        }
        super.setValues(kind, entry, state, readOnly, vig);
    }

    public boolean hasData() {
        ValuesDelta entry = getEntry();

        String family = entry.getAsString(StructuredName.PHONETIC_FAMILY_NAME);
        String middle = entry.getAsString(StructuredName.PHONETIC_MIDDLE_NAME);
        String given = entry.getAsString(StructuredName.PHONETIC_GIVEN_NAME);

        return !TextUtils.isEmpty(family) || !TextUtils.isEmpty(middle)
                || !TextUtils.isEmpty(given);
    }
}
