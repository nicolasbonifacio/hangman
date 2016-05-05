package com.nick.hangman;

import android.content.Context;

/**
 * Created by Nick on 2016-04-26.
 */
public class Utils {

    public Utils() {
    }

    public int getButtonCode(int percentage) {
        int buttonCode = 0;

        switch(percentage) {
            case -1: buttonCode = R.drawable.button_000_disabled; break;
            case 0: buttonCode = R.drawable.button_000; break;
            case 1: buttonCode = R.drawable.button_001; break;
            case 2: buttonCode = R.drawable.button_002; break;
            case 3: buttonCode = R.drawable.button_003; break;
            case 4: buttonCode = R.drawable.button_004; break;
            case 5: buttonCode = R.drawable.button_005; break;
            case 6: buttonCode = R.drawable.button_006; break;
            case 7: buttonCode = R.drawable.button_007; break;
            case 8: buttonCode = R.drawable.button_008; break;
            case 9: buttonCode = R.drawable.button_009; break;
            case 10: buttonCode = R.drawable.button_010; break;
            case 11: buttonCode = R.drawable.button_011; break;
            case 12: buttonCode = R.drawable.button_012; break;
            case 13: buttonCode = R.drawable.button_013; break;
            case 14: buttonCode = R.drawable.button_014; break;
            case 15: buttonCode = R.drawable.button_015; break;
            case 16: buttonCode = R.drawable.button_016; break;
            case 17: buttonCode = R.drawable.button_017; break;
            case 18: buttonCode = R.drawable.button_018; break;
            case 19: buttonCode = R.drawable.button_019; break;
            case 20: buttonCode = R.drawable.button_020; break;
            case 21: buttonCode = R.drawable.button_021; break;
            case 22: buttonCode = R.drawable.button_022; break;
            case 23: buttonCode = R.drawable.button_023; break;
            case 24: buttonCode = R.drawable.button_024; break;
            case 25: buttonCode = R.drawable.button_025; break;
            case 26: buttonCode = R.drawable.button_026; break;
            case 27: buttonCode = R.drawable.button_027; break;
            case 28: buttonCode = R.drawable.button_028; break;
            case 29: buttonCode = R.drawable.button_029; break;
            case 30: buttonCode = R.drawable.button_030; break;
            case 31: buttonCode = R.drawable.button_031; break;
            case 32: buttonCode = R.drawable.button_032; break;
            case 33: buttonCode = R.drawable.button_033; break;
            case 34: buttonCode = R.drawable.button_034; break;
            case 35: buttonCode = R.drawable.button_035; break;
            case 36: buttonCode = R.drawable.button_036; break;
            case 37: buttonCode = R.drawable.button_037; break;
            case 38: buttonCode = R.drawable.button_038; break;
            case 39: buttonCode = R.drawable.button_039; break;
            case 40: buttonCode = R.drawable.button_040; break;
            case 41: buttonCode = R.drawable.button_041; break;
            case 42: buttonCode = R.drawable.button_042; break;
            case 43: buttonCode = R.drawable.button_043; break;
            case 44: buttonCode = R.drawable.button_044; break;
            case 45: buttonCode = R.drawable.button_045; break;
            case 46: buttonCode = R.drawable.button_046; break;
            case 47: buttonCode = R.drawable.button_047; break;
            case 48: buttonCode = R.drawable.button_048; break;
            case 49: buttonCode = R.drawable.button_049; break;
            case 50: buttonCode = R.drawable.button_050; break;
            case 51: buttonCode = R.drawable.button_051; break;
            case 52: buttonCode = R.drawable.button_052; break;
            case 53: buttonCode = R.drawable.button_053; break;
            case 54: buttonCode = R.drawable.button_054; break;
            case 55: buttonCode = R.drawable.button_055; break;
            case 56: buttonCode = R.drawable.button_056; break;
            case 57: buttonCode = R.drawable.button_057; break;
            case 58: buttonCode = R.drawable.button_058; break;
            case 59: buttonCode = R.drawable.button_059; break;
            case 60: buttonCode = R.drawable.button_060; break;
            case 61: buttonCode = R.drawable.button_061; break;
            case 62: buttonCode = R.drawable.button_062; break;
            case 63: buttonCode = R.drawable.button_063; break;
            case 64: buttonCode = R.drawable.button_064; break;
            case 65: buttonCode = R.drawable.button_065; break;
            case 66: buttonCode = R.drawable.button_066; break;
            case 67: buttonCode = R.drawable.button_067; break;
            case 68: buttonCode = R.drawable.button_068; break;
            case 69: buttonCode = R.drawable.button_069; break;
            case 70: buttonCode = R.drawable.button_070; break;
            case 71: buttonCode = R.drawable.button_071; break;
            case 72: buttonCode = R.drawable.button_072; break;
            case 73: buttonCode = R.drawable.button_073; break;
            case 74: buttonCode = R.drawable.button_074; break;
            case 75: buttonCode = R.drawable.button_075; break;
            case 76: buttonCode = R.drawable.button_076; break;
            case 77: buttonCode = R.drawable.button_077; break;
            case 78: buttonCode = R.drawable.button_078; break;
            case 79: buttonCode = R.drawable.button_079; break;
            case 80: buttonCode = R.drawable.button_080; break;
            case 81: buttonCode = R.drawable.button_081; break;
            case 82: buttonCode = R.drawable.button_082; break;
            case 83: buttonCode = R.drawable.button_083; break;
            case 84: buttonCode = R.drawable.button_084; break;
            case 85: buttonCode = R.drawable.button_085; break;
            case 86: buttonCode = R.drawable.button_086; break;
            case 87: buttonCode = R.drawable.button_087; break;
            case 88: buttonCode = R.drawable.button_088; break;
            case 89: buttonCode = R.drawable.button_089; break;
            case 90: buttonCode = R.drawable.button_090; break;
            case 91: buttonCode = R.drawable.button_091; break;
            case 92: buttonCode = R.drawable.button_092; break;
            case 93: buttonCode = R.drawable.button_093; break;
            case 94: buttonCode = R.drawable.button_094; break;
            case 95: buttonCode = R.drawable.button_095; break;
            case 96: buttonCode = R.drawable.button_096; break;
            case 97: buttonCode = R.drawable.button_097; break;
            case 98: buttonCode = R.drawable.button_098; break;
            case 99: buttonCode = R.drawable.button_099; break;
            case 100: buttonCode = R.drawable.button_100; break;
            default: buttonCode = 0;

        }

        return buttonCode;
    }

    public int getArrowIconStars(int numStars) {

        int iconCode;

        switch (numStars) {
            case 0: iconCode = R.drawable.arrow_icon_0_stars;
                break;
            case 1: iconCode = R.drawable.arrow_icon_1_stars;
                break;
            case 2: iconCode = R.drawable.arrow_icon_2_stars;
                break;
            case 3: iconCode = R.drawable.arrow_icon_3_stars;
                break;
            default: iconCode = R.drawable.arrow_icon_0_stars;
        }

        return iconCode;
    }

    public int getGallowsImage(int numErrors) {

        int imageCode;

        switch (numErrors) {
            case 0: imageCode = R.drawable.img_0_gallows;
                break;
            case 1: imageCode = R.drawable.img_1_head;
                break;
            case 2: imageCode = R.drawable.img_2_body;
                break;
            case 3: imageCode = R.drawable.img_3_right_arm;
                break;
            case 4: imageCode = R.drawable.img_4_left_arm;
                break;
            case 5: imageCode = R.drawable.img_5_right_leg;
                break;
            case 6: imageCode = R.drawable.img_6_left_leg;
                break;
            default: imageCode = R.drawable.img_0_gallows;
        }

        return imageCode;
    }

    public int getGallowsImageWithImage(int numErrors) {

        int imageCode;

        switch (numErrors) {
            case 0: imageCode = R.drawable.gallows_0_image;
                break;
            case 1: imageCode = R.drawable.gallows_1_image_head;
                break;
            case 2: imageCode = R.drawable.gallows_2_image_body;
                break;
            case 3: imageCode = R.drawable.gallows_3_image_right_arm;
                break;
            case 4: imageCode = R.drawable.gallows_4_image_left_arm;
                break;
            case 5: imageCode = R.drawable.gallows_5_image_right_leg;
                break;
            case 6: imageCode = R.drawable.gallows_6_image_left_leg;
                break;
            default: imageCode = R.drawable.gallows_0_image;
        }

        return imageCode;
    }

    public int getCharacterImage(String character) {

        int imageCode;

        switch (character) {
            case "A":
                imageCode = R.drawable.character_a;
                break;
            case "B":
                imageCode = R.drawable.character_b;
                break;
            case "C":
                imageCode = R.drawable.character_c;
                break;
            case "D":
                imageCode = R.drawable.character_d;
                break;
            case "E":
                imageCode = R.drawable.character_e;
                break;
            case "F":
                imageCode = R.drawable.character_f;
                break;
            case "G":
                imageCode = R.drawable.character_g;
                break;
            case "H":
                imageCode = R.drawable.character_h;
                break;
            case "I":
                imageCode = R.drawable.character_i;
                break;
            case "J":
                imageCode = R.drawable.character_j;
                break;
            case "K":
                imageCode = R.drawable.character_k;
                break;
            case "L":
                imageCode = R.drawable.character_l;
                break;
            case "M":
                imageCode = R.drawable.character_m;
                break;
            case "N":
                imageCode = R.drawable.character_n;
                break;
            case "O":
                imageCode = R.drawable.character_o;
                break;
            case "P":
                imageCode = R.drawable.character_p;
                break;
            case "Q":
                imageCode = R.drawable.character_q;
                break;
            case "R":
                imageCode = R.drawable.character_r;
                break;
            case "S":
                imageCode = R.drawable.character_s;
                break;
            case "T":
                imageCode = R.drawable.character_t;
                break;
            case "U":
                imageCode = R.drawable.character_u;
                break;
            case "V":
                imageCode = R.drawable.character_v;
                break;
            case "W":
                imageCode = R.drawable.character_w;
                break;
            case "X":
                imageCode = R.drawable.character_x;
                break;
            case "Y":
                imageCode = R.drawable.character_y;
                break;
            case "Z":
                imageCode = R.drawable.character_z;
                break;
            case "DASH":
                imageCode = R.drawable.character_dash;
                break;
            default:
                imageCode = R.drawable.character_dash;
        }

        return imageCode;

    }
}
