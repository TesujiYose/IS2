package encryptdecrypt;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class EncryptSender {

    private OperationsMethod method;

    public void setMethod(OperationsMethod method) {
        this.method = method;
    }

    public String crypt(String msg, String key) {
        return this.method.crypt(msg, key);
    }

    public String decrypt(String msg, String key) {
        return this.method.decrypt(msg, key);
    }
}


interface OperationsMethod {

    String crypt(String msg, String key);

    String decrypt(String msg, String key);

}

class MorseMethod implements OperationsMethod {

    public MorseMethod() {
        initMap();
    }

    Map<String, String> morseCodeMap = new HashMap<>();


    @Override
    public String crypt(String msg, String key) {
        // System.out.println("Crypting via MorseMethod");
        msg = msg.replace(" ", "");
        StringBuffer encryptedMsg = new StringBuffer();
        for (int i = 0; i < msg.length(); i++) {
            encryptedMsg.append(morseCodeMap.get(String.valueOf(msg.charAt(i))));
            encryptedMsg.append(" ");
        }
        return String.valueOf(encryptedMsg);
    }

    @Override
    public String decrypt(String msg, String key) {
        //System.out.println("Decrypting via MorseMethod");
        StringBuffer decryptedMsg = new StringBuffer();

        for(String word : msg.split(" ")) {
            for (var entry : morseCodeMap.entrySet()) {
                if (entry.getValue().equals(word)){
                    decryptedMsg.append(entry.getKey());
                    break;
                }
            }
        }




        return String.valueOf(decryptedMsg);

    }

    private void initMap() {
        morseCodeMap.put("a", "*-");
        morseCodeMap.put("b", "-***");
        morseCodeMap.put("c", "-*");
        morseCodeMap.put("d", "-**");
        morseCodeMap.put("e", "*");
        morseCodeMap.put("f", "**-*");
        morseCodeMap.put("g", "--*");
        morseCodeMap.put("h", "****");
        morseCodeMap.put("i", "**");
        morseCodeMap.put("j", "*---");
        morseCodeMap.put("k", "-*-");
        morseCodeMap.put("l", "*-**");
        morseCodeMap.put("m", "--");
        morseCodeMap.put("n", "-*");
        morseCodeMap.put("o", "---");
        morseCodeMap.put("p", "*---*");
        morseCodeMap.put("q", "--*-");
        morseCodeMap.put("r", "*-*");
        morseCodeMap.put("s", "***");
        morseCodeMap.put("t", "-");
        morseCodeMap.put("u", "**-");
        morseCodeMap.put("v", "***-");
        morseCodeMap.put("w", "*--");
        morseCodeMap.put("x", "-**-");
        morseCodeMap.put("y", "-*--");
        morseCodeMap.put("z", "--**");
        morseCodeMap.put("1", "*----");
        morseCodeMap.put("2", "**---");
        morseCodeMap.put("3", "***--");
        morseCodeMap.put("4", "****-");
        morseCodeMap.put("5", "*****");
        morseCodeMap.put("6", "-****");
        morseCodeMap.put("7", "--***");
        morseCodeMap.put("8", "---**");
        morseCodeMap.put("9", "----*");
        morseCodeMap.put("0", "-----");
        morseCodeMap.put(".", "*-*-*-");
        morseCodeMap.put(",", "--**--");
        morseCodeMap.put("?", "**--**");
        morseCodeMap.put(":", "---***");
        morseCodeMap.put(";", "-*-*-*");
        morseCodeMap.put("-", "-***-");
        morseCodeMap.put("/", "-**-*");
        morseCodeMap.put("'", "*-**-*");
    }


}


class VigenereMethod implements OperationsMethod {
    final String smallAlphabet = "abcdefghijklmnopqrstuvwxyz";

    @Override
    public String crypt(String msg, String key) {
        StringBuffer encryptedMsg = new StringBuffer();
        key = key.toLowerCase();


        // System.out.println("Crypting via VigenereMethod");
        for (int i = 0; i < msg.length(); i++) {
            int index = smallAlphabet.indexOf(msg.charAt(i));
            index += smallAlphabet.indexOf(key.charAt(i % (key.length())));
            index %= smallAlphabet.length();
            encryptedMsg.append(smallAlphabet.charAt(index));
        }


        return String.valueOf(encryptedMsg);
    }

    @Override
    public String decrypt(String msg, String key) {
        // System.out.println("Decrypting via VigenereMethod");
        key = key.toLowerCase();
        StringBuffer decryptedMsg = new StringBuffer();

        for (int i = 0; i < msg.length(); i++) {
            int index = smallAlphabet.indexOf(msg.charAt(i));
            index -= smallAlphabet.indexOf(key.charAt(i % (key.length())));
            index += smallAlphabet.length();
            index %= smallAlphabet.length();
            decryptedMsg.append(smallAlphabet.charAt(index));
        }

        return String.valueOf(decryptedMsg);
    }

}

abstract class Encrypter {
    //def params
    private String[] args;
    String alg = "morse";
    String mode = "enc";
    String key = "";
    String data = "";
    boolean isData = false;
    boolean isOut = false;
    boolean isIn = false;
    String inPath = "";
    String outPath = "";
    String outputMsg = "";


    public Encrypter(String[] args) {
        this.args = args;
    }

    public void process() {
        readParams();
        doMagic();
        output();
    }

    public void readParams() {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-mode")) {
                mode = args[i + 1];
            }
            if (args[i].equals("-key")) {
                key = args[i + 1];
            }
            if (args[i].equals("-alg")) {
                alg = args[i + 1];
            }
            if (args[i].equals("-in")) {
                inPath = args[i + 1];
                isIn = true;
            }
            if (args[i].equals("-out")) {
                outPath = args[i + 1];
                isOut = true;
            }

            if (args[i].equals("-data")) {
                data = args[i + 1];
                data = data.toLowerCase();
                isData = true;
            }
        }

        if (!isData && isIn) {
            data = "";
            File file = new File(inPath);
            try (Scanner sc = new Scanner(file)) {
                while (sc.hasNext()) {
                    data = data + sc.nextLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void doMagic();

    public void output() {

        if (isOut) {
            File outFile = new File(outPath);
            try (PrintWriter printWriter = new PrintWriter(outFile)) {
                printWriter.println(outputMsg); // prints a string and then terminates the line
            } catch (IOException e) {
                System.out.printf("An exception occurs %s", e.getMessage());
            }
        } else {
            System.out.println(outputMsg);
        }
    }
}

class EncryptDecrypt extends Encrypter {


    public EncryptDecrypt(String[] args) {
        super(args);
    }

    @Override
    public void doMagic() {

        EncryptSender sender = new EncryptSender();


        switch (alg) {
            case "morse":
                sender.setMethod(new MorseMethod());
                break;
            case "vigenere":
                sender.setMethod(new VigenereMethod());
                break;
            default:
                System.out.println("Wrong -alg param");
        }

        switch (mode) {
            case "enc":
                outputMsg = sender.crypt(data, key);
                break;
            case "dec":
                outputMsg = sender.decrypt(data, key);
                break;
            default:
                System.out.println("Wrong -mode param");
        }
    }
}


public class Main {

    public static void main(String[] args) {
        String test[] = {"-data","LXFOPVEFRNHR",
                "-key","LEMON",
                "-mode","dec",
                "-alg","vigenere"
        };
        Encrypter method = new EncryptDecrypt(test);
        method.process();
    }
    //ATTACKATDAWN
    final String smallAlphabet = "abcdefghijklmnopqrstuvwxyz";

}

