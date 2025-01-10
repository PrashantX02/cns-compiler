package com.example.cnscompiler.codeView;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

public class codeView extends LinearLayout {

    private EditText codeEditor;
    private TextView lineNumbers;
    private ScrollView lineNumberScroll;
    private ScrollView codeEditorScroll;

    private Stack<String> u;
    private Stack<String> r;

    private String prev = null;

    private boolean isProgrammaticChange = false; // Flag to track programmatic text changes

    public codeView(Context context) {
        super(context);
        init(context);
        u = new Stack<>();
        r = new Stack<>();
    }

    public codeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        u = new Stack<>();
        r = new Stack<>();
    }

    public codeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        u = new Stack<>();
        r = new Stack<>();
    }

    private void init(Context context) {
        // Set the layout properties
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.parseColor("#2B2B2B")); // Dark background for code editor

        // Create the ScrollView for line numbers
        lineNumberScroll = new ScrollView(context);
        lineNumberScroll.setBackgroundColor(Color.parseColor("#1E1E1E"));
        lineNumberScroll.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        ));

        // Create the TextView for line numbers
        lineNumbers = new TextView(context);
        lineNumbers.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        ));
        lineNumbers.setBackgroundColor(Color.parseColor("#1E1E1E")); // Slightly darker background for line numbers
        lineNumbers.setTextColor(Color.GRAY);
        lineNumbers.setPadding(16, 16, 16, 16);
        lineNumbers.setTextSize(18f);
        lineNumbers.setText("1\n"); // Initial line number
        lineNumberScroll.addView(lineNumbers);

        // Create the ScrollView for the code editor
        codeEditorScroll = new ScrollView(context);
        LayoutParams editorScrollParams = new LayoutParams(
                0,
                LayoutParams.MATCH_PARENT,
                1.0f
        );
        codeEditorScroll.setLayoutParams(editorScrollParams);

        // Create the EditText for code editing
        codeEditor = new EditText(context);
        codeEditor.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
        codeEditor.setBackgroundColor(Color.parseColor("#2B2B2B"));
        codeEditor.setTextColor(Color.WHITE);
        codeEditor.setHintTextColor(Color.GRAY);
        codeEditor.setPadding(16, 16, 16, 16);
        codeEditor.setGravity(Gravity.TOP);
        codeEditor.setTextSize(18f);
        codeEditor.setHint("Start coding here...");
        codeEditor.setScrollContainer(false); // Prevent internal scrolling for EditText
        codeEditor.setVerticalScrollBarEnabled(false);
        codeEditorScroll.addView(codeEditor);

        // Update line numbers dynamically as user types
        codeEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only update line numbers and undo stack if it's a user input
                if (!isProgrammaticChange) {
                    updateLineNumbers();
                    if(prev != null) u.push(prev);
                    if (count > 0) prev = codeEditor.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Synchronize scrolling between line numbers and code editor
        lineNumberScroll.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) ->
                codeEditorScroll.scrollTo(scrollX, scrollY)
        );
        codeEditorScroll.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) ->
                lineNumberScroll.scrollTo(scrollX, scrollY)
        );

        // Add the line number ScrollView and code editor ScrollView to the layout
        addView(lineNumberScroll);
        addView(codeEditorScroll);
    }

    private void updateLineNumbers() {
        String[] lines = codeEditor.getText().toString().split("\n");
        StringBuilder lineNumberText = new StringBuilder();
        for (int i = 1; i <= lines.length; i++) {
            lineNumberText.append(i).append("\n");
        }
        lineNumbers.setText(lineNumberText.toString());
    }

    public String getCodeText() {
        return codeEditor.getText().toString();
    }

    public void undo() {
        Log.d("myt",u.toString());
        if (u.size() == 1) {
            String temp = u.peek();
            String toRedo = codeEditor.getText().toString();
            isProgrammaticChange = true;
            codeEditor.setText(temp);
            isProgrammaticChange = false;
            if(!r.isEmpty() && !r.peek().equals(toRedo)) r.push(toRedo);
            updateLineNumbers();
            return;
        }

        String temp = u.pop();
        String toRedo = codeEditor.getText().toString();
        isProgrammaticChange = true; // Disable the TextWatcher temporarily
        codeEditor.setText(temp);
        isProgrammaticChange = false; // Re-enable the TextWatcher
        r.push(toRedo);
        codeEditor.setSelection(codeEditor.getText().length());
        updateLineNumbers();
    }

    public void redo() {
        if (r.isEmpty()) return;
        String temp = r.pop();
        String toUndo = codeEditor.getText().toString();
        isProgrammaticChange = true; // Disable the TextWatcher temporarily
        codeEditor.setText(temp);
        isProgrammaticChange = false; // Re-enable the TextWatcher
        u.push(toUndo);
        codeEditor.setSelection(codeEditor.getText().length());
        updateLineNumbers();
    }

    public void clearStack() {
        r.clear();
        u.clear();
        u.push(codeEditor.getText().toString());
    }

    public void setCodeText(String code) {
        isProgrammaticChange = true;
        codeEditor.setText(code);
        isProgrammaticChange = false;
        updateLineNumbers();
        u.clear();
        r.clear();
        u.push(code);
    }

    public void setEditable(boolean editable) {
        codeEditor.setFocusable(editable);
        codeEditor.setFocusableInTouchMode(editable);
        updateLineNumbers();
    }
}
