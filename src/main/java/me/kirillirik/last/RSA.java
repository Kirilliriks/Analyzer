package me.kirillirik.last;

import imgui.ImGui;
import imgui.type.ImInt;
import me.kirillirik.Widget;

import java.math.BigInteger;

public final class RSA extends Widget {

    private final ImInt inputNum = new ImInt();
    private final ImInt inputCVal = new ImInt();
    private final ImInt inputEVal = new ImInt();

    private int num, eVal, dVal, cVal;
    private BigInteger c, n, p, q, d, sign, el, en, de, crs, des;

    @Override
    public void update() {
        ImGui.begin("RSA");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.inputInt("N", inputNum);
        ImGui.inputInt("E", inputEVal);
        ImGui.inputInt("C", inputCVal);

        if (ImGui.button("Решить")) {
            num = inputNum.get();
            cVal = inputCVal.get();
            eVal = inputEVal.get();
            analyze();
        }

        ImGui.text("p: " + p);
        ImGui.text("q: " + q);
        ImGui.text("Значение функции Эйлера: " + el);
        ImGui.text("d: " + d);
        ImGui.text("Расшифрованное сообщение: " + de);
        ImGui.text("Еще раз зашифрованное сообщение: " + en);
        ImGui.text("Подпись: " + crs);
        ImGui.text("Расшифрованная подпись: " + des);

        ImGui.end();
    }

    @Override
    public void analyze() {
        if (num == 0) {
            return;
        }

        c = BigInteger.valueOf(cVal);
        n = BigInteger.valueOf(num);
        int t = 0;

        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                t = i;
                break;
            }
        }
        dVal = 1;
        while ((eVal * dVal) % ((t - 1) * ((num/t) - 1)) != 1) {
            dVal++;
        }

        p = BigInteger.valueOf(t);
        q = n.divide(p);
        d = BigInteger.valueOf(dVal);
        el = (p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1)));

        System.out.println("Зашифрованное сообщение: " + c);
        System.out.println("n: " + n);
        System.out.println("p: " + p);
        System.out.println("q: " + q);
        System.out.println("Значение функции Эйлера: " + el);
        System.out.println("e: " + eVal);
        System.out.println("d: " + d);


        System.out.println("Расшифрованное сообщение: " + decrypt());
        System.out.println("Еще раз зашифрованное сообщение: " + encrypt());

        createSign();
        decryptSign();
    }

    public BigInteger decrypt() {
        c = c.pow(dVal).mod(n);
        de = c;
        return c;
    }

    public BigInteger encrypt() {
        c = c.pow(eVal).mod(n);
        en = c;
        return c;
    }

    public void createSign() {
        decrypt();
        sign = c.pow(dVal).mod(n);
        crs = sign;
        System.out.println("Подпись: " + sign);
    }

    public void decryptSign() {
        sign = sign.pow(eVal).mod(n);
        des = sign;
        System.out.println("Расшифрованная подпись: " + sign);
    }
}
