package com.hanvon.sulupen.pinyin;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;

public class MyInputConnection implements InputConnection {
public StringBuffer curInputStr;
	@Override
	public boolean beginBatchEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean clearMetaKeyStates(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean commitCompletion(CompletionInfo arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean commitCorrection(CorrectionInfo arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean commitText(CharSequence arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSurroundingText(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean endBatchEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finishComposingText() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCursorCapsMode(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ExtractedText getExtractedText(ExtractedTextRequest arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getSelectedText(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getTextAfterCursor(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharSequence getTextBeforeCursor(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean performContextMenuAction(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performEditorAction(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performPrivateCommand(String arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean reportFullscreenMode(boolean arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendKeyEvent(KeyEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setComposingRegion(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setComposingText(CharSequence arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setSelection(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
