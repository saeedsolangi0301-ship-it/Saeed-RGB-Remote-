package com.custom.rgbremote;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ConsumerIrManager irManager;
    private TextView statusText;
    private LinearLayout mainGridContainer;
    private LinearLayout extraGridContainer;
    private Button tabMainBtn;
    private Button tabExtraBtn;

    private static class IrButtonDef {
        final String label;
        final int cmd;
        final String bgHex;
        final String fgHex;

        IrButtonDef(String label, int cmd, String bgHex, String fgHex) {
            this.label = label;
            this.cmd = cmd;
            this.bgHex = bgHex;
            this.fgHex = fgHex;
        }
    }

    private static final IrButtonDef[] MAIN_BUTTONS = new IrButtonDef[] {
        new IrButtonDef("Bright +",    0x00, "#E0E0E0", "#000000"),
        new IrButtonDef("Bright -",    0x01, "#BDBDBD", "#000000"),
        new IrButtonDef("OFF",         0x02, "#212121", "#FFFFFF"),
        new IrButtonDef("ON",          0x03, "#D32F2F", "#FFFFFF"),

        new IrButtonDef("Red",         0x04, "#FF0000", "#FFFFFF"),
        new IrButtonDef("Green",       0x05, "#00C853", "#FFFFFF"),
        new IrButtonDef("Blue",        0x06, "#0000FF", "#FFFFFF"),
        new IrButtonDef("White",       0x07, "#FFFFFF", "#000000"),

        new IrButtonDef("Red Orange",  0x08, "#FF3D00", "#FFFFFF"),
        new IrButtonDef("Light Green", 0x09, "#76FF03", "#000000"),
        new IrButtonDef("Dark Blue",   0x0A, "#1A237E", "#FFFFFF"),
        new IrButtonDef("Flash",       0x0B, "#546E7A", "#FFFFFF"),

        new IrButtonDef("Orange",      0x0C, "#FF9100", "#000000"),
        new IrButtonDef("Cyan",        0x0D, "#00E5FF", "#000000"),
        new IrButtonDef("Purple",      0x0E, "#6A1B9A", "#FFFFFF"),
        new IrButtonDef("Strobe",      0x0F, "#546E7A", "#FFFFFF"),

        new IrButtonDef("Amber",       0x10, "#FFC400", "#000000"),
        new IrButtonDef("Teal",        0x11, "#009688", "#FFFFFF"),
        new IrButtonDef("Violet",      0x12, "#AA00FF", "#FFFFFF"),
        new IrButtonDef("Fade",        0x13, "#546E7A", "#FFFFFF"),

        new IrButtonDef("Yellow",      0x14, "#FFFF00", "#000000"),
        new IrButtonDef("Sky Blue",    0x15, "#29B6F6", "#000000"),
        new IrButtonDef("Pink",        0x16, "#FF4081", "#FFFFFF"),
        new IrButtonDef("Smooth",      0x17, "#546E7A", "#FFFFFF")
    };

    private static final IrButtonDef[] EXTRA_BUTTONS = new IrButtonDef[] {
        new IrButtonDef("Warm White",  0x48, "#FFF3E0", "#000000"),
        new IrButtonDef("Cool White",  0x4C, "#E3F2FD", "#000000"),
        new IrButtonDef("Pink White",  0x1F, "#FCE4EC", "#000000"),
        new IrButtonDef("Ice White",   0x1B, "#E0F7FA", "#000000"),

        new IrButtonDef("Crimson",     0x58, "#C62828", "#FFFFFF"),
        new IrButtonDef("Lime",        0x59, "#AEEA00", "#000000"),
        new IrButtonDef("Cobalt",      0x45, "#283593", "#FFFFFF"),
        new IrButtonDef("Coral",       0x54, "#FF7043", "#000000"),

        new IrButtonDef("Mint",        0x55, "#69F0AE", "#000000"),
        new IrButtonDef("Indigo",      0x49, "#3F51B5", "#FFFFFF"),
        new IrButtonDef("Tangerine",   0x50, "#FF6D00", "#FFFFFF"),
        new IrButtonDef("Aqua",        0x51, "#18FFFF", "#000000"),

        new IrButtonDef("Magenta",     0x4D, "#E040FB", "#FFFFFF"),
        new IrButtonDef("Gold",        0x1C, "#FFD740", "#000000"),
        new IrButtonDef("Turquoise",   0x1D, "#26C6DA", "#000000"),
        new IrButtonDef("Hot Pink",    0x1E, "#F50057", "#FFFFFF"),

        new IrButtonDef("Lemon",       0x18, "#EEFF41", "#000000"),
        new IrButtonDef("Deep Teal",   0x19, "#00695C", "#FFFFFF"),
        new IrButtonDef("Lavender",    0x1A, "#B388FF", "#000000"),
        new IrButtonDef("Play/Pause",  0x41, "#424242", "#FFFFFF")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        irManager = (ConsumerIrManager) getSystemService(Context.CONSUMER_IR_SERVICE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#11131A"));
        root.setPadding(dp(14), dp(18), dp(14), dp(14));

        TextView title = new TextView(this);
        title.setText("RGB LED Remote");
        title.setTextColor(Color.WHITE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        statusText = new TextView(this);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(dp(12), dp(6), dp(12), dp(6));
        LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        statusLp.gravity = Gravity.CENTER_HORIZONTAL;
        statusLp.topMargin = dp(6);
        statusLp.bottomMargin = dp(12);
        statusText.setLayoutParams(statusLp);

        boolean hasIr = (irManager != null && irManager.hasIrEmitter());
        statusText.setText(hasIr ? "IR Blaster Ready" : "No IR Blaster Found");
        statusText.setTextColor(hasIr ? Color.parseColor("#69F0AE") : Color.parseColor("#FF5252"));
        GradientDrawable pillBg = new GradientDrawable();
        pillBg.setColor(Color.parseColor("#1E2230"));
        pillBg.setCornerRadius(dp(20));
        statusText.setBackground(pillBg);
        root.addView(statusText);

        LinearLayout tabRow = new LinearLayout(this);
        tabRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams tabRowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tabRowLp.bottomMargin = dp(12);
        tabRow.setLayoutParams(tabRowLp);

        tabMainBtn = createTabButton("24-Key Main", true);
        tabExtraBtn = createTabButton("Extra Colors", false);

        tabMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(true);
            }
        });
        tabExtraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(false);
            }
        });

        tabRow.addView(tabMainBtn);
        tabRow.addView(tabExtraBtn);
        root.addView(tabRow);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout contentWrapper = new LinearLayout(this);
        contentWrapper.setOrientation(LinearLayout.VERTICAL);

        mainGridContainer = buildButtonGrid(MAIN_BUTTONS, 4);
        extraGridContainer = buildButtonGrid(EXTRA_BUTTONS, 4);
        extraGridContainer.setVisibility(View.GONE);

        contentWrapper.addView(mainGridContainer);
        contentWrapper.addView(extraGridContainer);
        scrollView.addView(contentWrapper);
        root.addView(scrollView);

        setContentView(root);
    }

    private Button createTabButton(String text, boolean active) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(42), 1f);
        lp.setMargins(dp(4), 0, dp(4), 0);
        btn.setLayoutParams(lp);
        styleTabButton(btn, active);
        return btn;
    }

    private void styleTabButton(Button btn, boolean active) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(12));
        bg.setColor(Color.parseColor(active ? "#2979FF" : "#1E2230"));
        btn.setBackground(bg);
        btn.setTextColor(active ? Color.WHITE : Color.parseColor("#B0BEC5"));
    }

    private void switchTab(boolean showMain) {
        mainGridContainer.setVisibility(showMain ? View.VISIBLE : View.GONE);
        extraGridContainer.setVisibility(showMain ? View.GONE : View.VISIBLE);
        styleTabButton(tabMainBtn, showMain);
        styleTabButton(tabExtraBtn, !showMain);
    }

    private LinearLayout buildButtonGrid(IrButtonDef[] defs, int columns) {
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < defs.length; i += columns) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowLp.bottomMargin = dp(8);
            row.setLayoutParams(rowLp);

            for (int c = 0; c < columns; c++) {
                int idx = i + c;
                if (idx < defs.length) {
                    row.addView(createIrButton(defs[idx]));
                } else {
                    View spacer = new View(this);
                    spacer.setLayoutParams(new LinearLayout.LayoutParams(0, dp(64), 1f));
                    row.addView(spacer);
                }
            }
            grid.addView(row);
        }
        return grid;
    }

    private Button createIrButton(final IrButtonDef def) {
        Button btn = new Button(this);
        btn.setText(def.label);
        btn.setAllCaps(false);
        btn.setTextColor(Color.parseColor(def.fgHex));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setPadding(dp(4), dp(4), dp(4), dp(4));

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(def.bgHex));
        shape.setCornerRadius(dp(14));
        shape.setStroke(dp(1), Color.parseColor("#33FFFFFF"));

        RippleDrawable ripple = new RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#55FFFFFF")),
                shape,
                null
        );
        btn.setBackground(ripple);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(68), 1f);
        lp.setMargins(dp(4), 0, dp(4), 0);
        btn.setLayoutParams(lp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                transmitIr(def.label, def.cmd);
            }
        });
        return btn;
    }

    private void transmitIr(String label, int cmd) {
        if (irManager == null || !irManager.hasIrEmitter()) {
            statusText.setText("No IR Blaster found on this phone");
            Toast.makeText(this, "Phone does not have an IR Blaster", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int[] pattern = buildNecPattern(0x00, 0xFF, cmd);
            irManager.transmit(38000, pattern);
            statusText.setText("Sent: " + label);
        } catch (Exception e) {
            statusText.setText("Error: " + e.getMessage());
        }
    }

    private int[] buildNecPattern(int addr0, int addr1, int cmd) {
        int invCmd = (~cmd) & 0xFF;
        int[] bytes = new int[] { addr0 & 0xFF, addr1 & 0xFF, cmd & 0xFF, invCmd };
        int[] pattern = new int[68];
        int idx = 0;

        pattern[idx++] = 9000;
        pattern[idx++] = 4500;

        for (int b : bytes) {
            for (int bit = 0; bit < 8; bit++) {
                pattern[idx++] = 560;
                pattern[idx++] = (((b >> bit) & 1) == 1) ? 1690 : 560;
            }
        }

        pattern[idx++] = 560;
        pattern[idx++] = 40000;
        return pattern;
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        ));
    }
                        }
          
