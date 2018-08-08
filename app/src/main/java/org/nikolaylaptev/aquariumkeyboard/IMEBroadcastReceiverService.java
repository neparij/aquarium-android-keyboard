package org.nikolaylaptev.aquariumkeyboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

import java.util.Objects;

public class IMEBroadcastReceiverService extends InputMethodService {

    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    private String INTENTFILTER_CHARSEQUENCE = "IME_INPUT_CHARS";
    private String INTENTFILTER_TEXT = "IME_INPUT_TEXT";
    private String INTENTFILTER_KEYCODE = "IME_INPUT_KEYCODE";
    private String INTENTFILTER_IMECODE = "IME_INPUT_IMECODE";
    private String INTENTFILTER_ACTION_SELECT = "IME_INPUT_ACTION_SELECT";
    private String INTENTFILTER_ACTION_CLEAR = "IME_INPUT_ACTION_CLEAR";

    @Override
    public View onCreateInputView() {
        if (mReceiver == null || mFilter == null) {
            mReceiver = createBroadcastReceiver();
            mFilter = createIntentFilter();

            registerReceiver(mReceiver, mFilter);
        }

        return getLayoutInflater().inflate(R.layout.default_view, null);
    }


    private BroadcastReceiver createBroadcastReceiver() {
        return new IMEReceiver();
    }

    private IntentFilter createIntentFilter() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(INTENTFILTER_CHARSEQUENCE);
        mFilter.addAction(INTENTFILTER_TEXT);
        mFilter.addAction(INTENTFILTER_KEYCODE);
        mFilter.addAction(INTENTFILTER_IMECODE);
        mFilter.addAction(INTENTFILTER_ACTION_SELECT);
        mFilter.addAction(INTENTFILTER_ACTION_CLEAR);
        return mFilter;
    }

    public void onDestroy() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    class IMEReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), INTENTFILTER_TEXT)) {
                String text = intent.getStringExtra("text");
                if (text != null) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.commitText(text, 1);
                }
            }

            if (Objects.equals(intent.getAction(), INTENTFILTER_CHARSEQUENCE)) {
                int[] chars = intent.getIntArrayExtra("charsequence");
                if (chars != null) {
                    String text = new String(chars, 0, chars.length);
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.commitText(text, 1);
                }
            }

            if (Objects.equals(intent.getAction(), INTENTFILTER_KEYCODE)) {
                int keycode = intent.getIntExtra("keycode", -1);
                if (keycode != -1) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
                }
            }

            if (Objects.equals(intent.getAction(), INTENTFILTER_IMECODE)) {
                int imecode = intent.getIntExtra("imecode", -1);
                if (imecode != -1) {
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null)
                        ic.performEditorAction(imecode);
                }
            }

            if (Objects.equals(intent.getAction(), INTENTFILTER_ACTION_SELECT)) {
                int start = intent.getIntExtra("start", -1);
                int end = intent.getIntExtra("end", -1);
                int position = intent.getIntExtra("position", -1);
                boolean all = intent.getBooleanExtra("all", false);
                if (all) {
                    InputConnection ic = getCurrentInputConnection();

                    if (ic != null) {
                        ExtractedText text = ic.getExtractedText(new ExtractedTextRequest(), 0);
                        ic.setSelection(0, text.text.length());
                    }
                } else if (start != -1 && end != -1) {
                    InputConnection ic = getCurrentInputConnection();

                    if (ic != null) {
                        ic.setSelection(start, end);
                    }
                } else if (position != -1) {
                    InputConnection ic = getCurrentInputConnection();

                    if (ic != null) {
                        ic.setSelection(position, position);
                    }
                }
            }

            if (Objects.equals(intent.getAction(), INTENTFILTER_ACTION_CLEAR)) {
                InputConnection ic = getCurrentInputConnection();
                if (ic != null) {
                    ExtractedText text = ic.getExtractedText(new ExtractedTextRequest(), 0);
                    ic.setSelection(0, text.text.length());
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        }
    }
}
