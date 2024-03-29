package com.example.qr_project;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class HashTest {

    // Copied from Hash implementation since the method is private static and can't be tested
    // directly
    private int calculateScore(String hash){
        int n = hash.length();
        int i = 0;
        int j;
        int k; // # of occurrences of the same letter in a row. I.e. 1 for a, 3 for aaa etc
        int score = 0;
        int base;


        while (i < n){
            j = i + 1;
            k = 1;
            while (j < n && hash.charAt(i) == hash.charAt(j)) {
                j++;
                k++;
            }
            if (hash.charAt(i) == '0'){
                base = 20;
            } else{
                base = "0123456789abcdef".indexOf(hash.charAt(i));
            }
            score += Math.pow(base, k-1);
            i = j;
        }

        return score;
    }

    // Also copied from Hash implementation since the method is private static and can't be tested
    // directly
    public static String generateName(String hashStr) {
        // Convert hash string to binary representation

        // Define dictionaries for each hexadecimal
        Map<Character, String> hex0Dict = new HashMap<>();
        hex0Dict.put('0', "ethereal");
        hex0Dict.put('1', "neon");
        hex0Dict.put('2', "icy");
        hex0Dict.put('3', "radiant");
        hex0Dict.put('4', "obsidian");
        hex0Dict.put('5', "gossamer");
        hex0Dict.put('6', "vermilion");
        hex0Dict.put('7', "mystic");
        hex0Dict.put('8', "dusky");
        hex0Dict.put('9', "cobalt");
        hex0Dict.put('a', "cerulean");
        hex0Dict.put('b', "crimson");
        hex0Dict.put('c', "golden");
        hex0Dict.put('d', "rustic");
        hex0Dict.put('e', "cosmic");
        hex0Dict.put('f', "emerald");

        Map<Character, String> hex1Dict = new HashMap<>();
        hex1Dict.put('0', "Fro");
        hex1Dict.put('1', "Glo");
        hex1Dict.put('2', "Blu");
        hex1Dict.put('3', "Sly");
        hex1Dict.put('4', "Mau");
        hex1Dict.put('5', "Fiz");
        hex1Dict.put('6', "Sky");
        hex1Dict.put('7', "Dew");
        hex1Dict.put('8', "Hue");
        hex1Dict.put('9', "Zap");
        hex1Dict.put('a', "Jaz");
        hex1Dict.put('b', "Jem");
        hex1Dict.put('c', "Lux");
        hex1Dict.put('d', "Aur");
        hex1Dict.put('e', "Flu");
        hex1Dict.put('f', "Nim");

        // Define dictionary for bit 2
        Map<Character, String> hex2Dict = new HashMap<>();
        hex2Dict.put('0', "Mo");
        hex2Dict.put('1', "Lyo");
        hex2Dict.put('2', "Dax");
        hex2Dict.put('3', "Bix");
        hex2Dict.put('4', "Jyn");
        hex2Dict.put('5', "Taz");
        hex2Dict.put('6', "Nyx");
        hex2Dict.put('7', "Rex");
        hex2Dict.put('8', "Giz");
        hex2Dict.put('9', "Vyn");
        hex2Dict.put('a', "Pax");
        hex2Dict.put('b', "Hix");
        hex2Dict.put('c', "Kaz");
        hex2Dict.put('d', "Wex");
        hex2Dict.put('e', "Yon");
        hex2Dict.put('f', "Zyx");

        // Define dictionary for bit 3
        Map<Character, String> hex3Dict = new HashMap<>();
        hex3Dict.put('0', "Omega");
        hex3Dict.put('1', "Giga");
        hex3Dict.put('2', "Tera");
        hex3Dict.put('3', "Eternal");
        hex3Dict.put('4', "Nova");
        hex3Dict.put('5', "Hyper");
        hex3Dict.put('6', "Endless");
        hex3Dict.put('7', "Epic");
        hex3Dict.put('8', "Titan");
        hex3Dict.put('9', "Myriad");
        hex3Dict.put('a', "Galactic");
        hex3Dict.put('b', "Supreme");
        hex3Dict.put('c', "Super");
        hex3Dict.put('d', "Ultimate");
        hex3Dict.put('e', "Legendary");
        hex3Dict.put('f', "Master");

        // Define dictionary for bit 4
        Map<Character, String> hex4Dict = new HashMap<>();
        hex4Dict.put('0', "Thunderous");
        hex4Dict.put('1', "Blazing");
        hex4Dict.put('2', "Divine");
        hex4Dict.put('3', "Infernal");
        hex4Dict.put('4', "Empyreal");
        hex4Dict.put('5', "Arcane");
        hex4Dict.put('6', "Exalted");
        hex4Dict.put('7', "Champion");
        hex4Dict.put('8', "Seraphic");
        hex4Dict.put('9', "Electric");
        hex4Dict.put('a', "Astral");
        hex4Dict.put('b', "Eclipse");
        hex4Dict.put('c', "Sonic");
        hex4Dict.put('d', "Spectral");
        hex4Dict.put('e', "Vanquished");
        hex4Dict.put('f', "Celestial");

        Map<Character, String> hex5Dict = new HashMap<>();
        hex5Dict.put('0', "Golem");
        hex5Dict.put('1', "Yeti");
        hex5Dict.put('2', "Gargoyle");
        hex5Dict.put('3', "Werewolf");
        hex5Dict.put('4', "Vampire");
        hex5Dict.put('5', "Gorgon");
        hex5Dict.put('6', "Chimera");
        hex5Dict.put('7', "Unicorn");
        hex5Dict.put('8', "Basilisk");
        hex5Dict.put('9', "Manticore");
        hex5Dict.put('a', "Wyvern");
        hex5Dict.put('b', "Cockatrice");
        hex5Dict.put('c', "Griffin");
        hex5Dict.put('d', "Wraith");
        hex5Dict.put('e', "Hydra");
        hex5Dict.put('f', "Leviathan");


        // Lookup values in each dictionary based on corresponding hexadecimals in the hash
        Character[] hexaDecimals = new Character[6];
        for (int i = 0; i < 6; i++) {
            hexaDecimals[i] = hashStr.charAt(i);
        }
        return hex0Dict.get(hexaDecimals[0]) + " " +
                hex1Dict.get(hexaDecimals[1]) +
                hex2Dict.get(hexaDecimals[2]) +
                hex3Dict.get(hexaDecimals[3]) +
                hex4Dict.get(hexaDecimals[4]) +
                hex5Dict.get(hexaDecimals[5]);
    }

    // Added for the same reasons as above functions
    private static String generateFace(String hashStr) {
        // Choose eyes
        Map<Character, String> hex2Eyes = new HashMap<>();
        hex2Eyes.put('0', "$ $");
        hex2Eyes.put('1', "* *");
        hex2Eyes.put('2', "O O");
        hex2Eyes.put('3', "^ ^");
        hex2Eyes.put('4', "~ ~");
        hex2Eyes.put('5', "U U");
        hex2Eyes.put('6', "' '");
        hex2Eyes.put('7', "X X");
        hex2Eyes.put('8', "> <");
        hex2Eyes.put('9', "# #");
        hex2Eyes.put('a', "- -");
        hex2Eyes.put('b', ". .");
        hex2Eyes.put('c', "F U");
        hex2Eyes.put('d', "= =");
        hex2Eyes.put('e', "$ $");
        hex2Eyes.put('f', "♥ ♥");


        // Choose ears
        Map<Character, String> hex2Ears = new HashMap<>();
        hex2Ears.put('0', "$$");      // dollar ears (high score)
        hex2Ears.put('1', "oo");   // round ears
        hex2Ears.put('2', "||");   // long ears
        hex2Ears.put('3', "[]");   // bat ears
        hex2Ears.put('4', "/\\");   // pointy ears
        hex2Ears.put('5', "()");   // elf ears
        hex2Ears.put('6', "!!");   // floppy ears
        hex2Ears.put('7', "@@");   // antenna ears
        hex2Ears.put('8', "\\/");   // wing ears
        hex2Ears.put('9', "~~");   // cat ears
        hex2Ears.put('a', "><");  // robot ears
        hex2Ears.put('b', "**");  // elephant ears
        hex2Ears.put('c', "<>");  // arrow ears
        hex2Ears.put('d', "==");  // rabbit ears
        hex2Ears.put('e', "__");  // devil ears
        hex2Ears.put('f', "##");  // short ears


        // Choose nose
        Map<Character, String> hex2Nose = new HashMap<>();
        hex2Nose.put('0', " $ ");    // dollar nose (high score)
        hex2Nose.put('1', " , ");   // small nose
        hex2Nose.put('2', " | ");   // straight nose
        hex2Nose.put('3', " /\\");   // curved nose
        hex2Nose.put('4', " | ");    // button nose
        hex2Nose.put('5', ". .");   // pig nose
        hex2Nose.put('6', "\\/ ");   // flared nostrils
        hex2Nose.put('7', "/\\_");   // hawk nose
        hex2Nose.put('8', "\\__");  // upturned nose
        hex2Nose.put('9', "\\\\\\");  // pointed nose
        hex2Nose.put('a', "<=>");   // wide nose
        hex2Nose.put('b', "(((");   // bulbous nose
        hex2Nose.put('c', " 0 ");   // Circle nose
        hex2Nose.put('d', "(_)");   // pudgy nose
        hex2Nose.put('e', " V ");  // ski slope nose
        hex2Nose.put('f', " + ");   // cleft nose


        // Choose mouth
        Map<Character, String> hex2Mouth = new HashMap<>();
        hex2Mouth.put('0', "$$$");      // dollar mouth (high score)
        hex2Mouth.put('1', " o ");   // small mouth
        hex2Mouth.put('2', " O ");   // oval mouth
        hex2Mouth.put('3', " ^ ");   // triangle mouth
        hex2Mouth.put('4', " U ");   // square mouth
        hex2Mouth.put('5', " V ");   // trapezoid mouth
        hex2Mouth.put('6', " | ");   // vertical line mouth
        hex2Mouth.put('7', "---");   // horizontal line mouth
        hex2Mouth.put('8', " S ");   // smile mouth
        hex2Mouth.put('9', " D ");   // frown mouth
        hex2Mouth.put('a', " 3 ");  // surprised mouth
        hex2Mouth.put('b', " P ");  // puckered mouth
        hex2Mouth.put('c', "___");  // neutral mouth
        hex2Mouth.put('d', " @ ");  // kissing mouth
        hex2Mouth.put('e', " X ");  // lips together mouth
        hex2Mouth.put('f', " + ");  // smirk mouth


        // Build head
        String eyes = hex2Eyes.get(hashStr.charAt(0));
        String ears = hex2Ears.get(hashStr.charAt(1));
        String nose = hex2Nose.get(hashStr.charAt(2));
        String mouth = hex2Mouth.get(hashStr.charAt(3));
        String head =
                " /‾‾‾‾‾\\ \n" +
                ears.charAt(0) + "| " + eyes + " |" + ears.charAt(1) + " \n" +
                " | " + nose + " | \n" +
                "|  " + mouth + "  |\n" +
                " \\_____/ ";
        return head;
    }

    /*
    The calculateScore can be used within the class only and assumes all strings are of hash
    type already. That is, it can have characters from 0 to 9 and from a to f. One exception is
    when NoSuchAlgorithmException is thrown at generateHash function which returns just an empty
    string. In this case, the score is set to 0. The generateName function might be updated
    in the future to exclude this possibility by using some additional libraries rather than
    built-in ones.
    */
    @Test
    void testCalculateScore(){

        // Partial hashes with 0 base
        String hashStr1 = "0";              int score1 = 1;
        String hashStr2 = "00";             int score2 = 20;
        String hashStr3 = "000";            int score3 = 400;
        String hashStr4 = "0000";           int score4 = 8000;

        // Partial hashes with 1 base
        String hashStr5 = "11";             int score5 = 1;
        String hashStr6 = "111";            int score6 = 1;
        String hashStr7 = "1111";           int score7 = 1;

        // Partial hashes with a base
        String hashStr8 = "aa";             int score8 = 10;
        String hashStr9 = "aaa";            int score9 = 100;

        // Mixed partial hashes
        String hashStr10 = "0123456789abcdef";   int score10 = 16;
        String hashStr11 = "000111aa1ffe";       int score11 = 428;

        // Biggest possible score
        String hashStr12 = "0000000000000000000000000000000000000000000000000000000000000000";
        int score12 = (int) Math.pow(20, 63);

        // Smallest possible score
        String hashStr13 = "1111111111111111111111111111111111111111111111111111111111111111";
        int score13 = 1;

        // Error score
        String hashStr14 = "";
        int score14 = 0;

        String[] hashes = {hashStr1, hashStr2, hashStr3, hashStr4, hashStr5, hashStr6, hashStr7,
                hashStr8, hashStr9, hashStr10, hashStr11, hashStr12, hashStr13, hashStr14};
        int[] scores = {score1, score2, score3, score4, score5, score6, score7, score8, score9,
                score10, score11, score12, score13, score14};

        for (int i = 0; i < hashes.length; i++) {
            assertEquals(scores[i], calculateScore(hashes[i]));
        }
    }

    @Test
    void testGenerateName(){
        // Testing a few names
        String hash1 = "696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6";
        String name1 = "vermilion ZapNyxSuperVanquishedVampire";

        String hash2 = "000000";
        String name2 = "ethereal FroMoOmegaThunderousGolem";

        String hash3 = "012345";
        String name3 = "ethereal GloDaxEternalEmpyrealGorgon";

        String hash4 = "012345aaaaaaaaaaaaaaaaa";
        String name4 = "ethereal GloDaxEternalEmpyrealGorgon";

        assertEquals(generateName(hash1), name1);
        assertEquals(generateName(hash2), name2);
        assertEquals(generateName(hash3), name3);
        assertEquals(generateName(hash4), name3);

    }

    @Test
    void testGenerateFace(){
        // Testing a few faces
        String hash1 = "0123";
        String hash2 = "4567";
        String hash3 = "abcd";
        String hash4 = "fffff";
        String hash5 = "111c";
        String hash6 = "abab";
        String hash7 = "f9e7";
        String[] hashes = {hash1, hash2, hash3, hash4, hash5, hash6, hash7};
        for (String hash:hashes) {
            System.out.println(generateFace(hash));
        }

    }
}
