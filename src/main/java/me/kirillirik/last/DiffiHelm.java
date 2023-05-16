package me.kirillirik.last;

import imgui.ImGui;
import imgui.type.ImInt;
import me.kirillirik.Widget;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class DiffiHelm extends Widget {

    private final ImInt inputX = new ImInt();
    private final ImInt inputModule = new ImInt();
    private final List<Integer> list = new ArrayList<>();

    private int module = -1;
    private long x;
    private long y;
    private long kx;
    private long ky;
    private BigInteger a, b;

    @Override
    public void update() {
        ImGui.begin("Diffie Helman");

        if (ImGui.button("Change analyzer")) {
            needClose = true;
        }

        ImGui.inputInt("P", inputModule);
        ImGui.inputInt("Ввести секретный ключ участника A: ", inputX);

        if (ImGui.button("Решить")) {
            module = inputModule.get();
            x = inputX.get();
            analyze();
        }

        ImGui.text("Секретный ключ участника В: " + y);
        ImGui.text("Открытый ключ участника А: " + kx);
        ImGui.text("Открытый ключ участника B: " + ky);
        ImGui.text("Обменный ключ участника А: " + a);
        ImGui.text("Обменный ключ участника B: " + b);

        ImGui.end();
    }

    @Override
    public void analyze() {
        if (module == -1) {
            return;
        }

        list.clear();

        final int length = module - 1;
        final int[] mas = new int[module + 1];

        int deg;
        int mem = 2;
        boolean stackFull = false;

        while (mem < length) {
            int num = mem;
            deg = 0;

            while (deg != length) {
                int res = 1;
                deg = 0;

                for (int i = 0; i < length; i++) {
                    mas[i] = 0;
                }

                do {
                    res *= num;
                    res %= module;
                    deg++;

                    if (deg > module) {
                        res = 1;
                        stackFull = true;
                        continue;
                    }

                    mas[deg] = res;
                } while (res != 1);

                num++;
            }

            list.add(num - 1);

            for (int i = 0; i < length; i++) {
                for (int j = 0; j < length; j++) {
                    if ((i != j) && (mas[j] == mas[i])) {
                        System.out.println("Неправильно");
                    }
                }
                mem = num;
            }
        }

        if (stackFull) {
            list.remove(list.size() - 1);
        }

        System.out.println(list);

        prepareOutput();
    }

    private void prepareOutput() {
        System.out.println("Секретный ключ участника A: " + x);
        y = 35 - x;
        System.out.println("Секретный ключ участника В: " + y);

        kx = (long) (Math.pow(list.get(0), x) % module);
        System.out.println("Открытый ключ участника А: " + kx);
        ky = (long) (Math.pow(list.get(0), y) % module);
        System.out.println("Открытый ключ участника В: " + ky);


        a = BigInteger.valueOf(ky).pow((int) x).mod(BigInteger.valueOf(module));
        System.out.println("Обменный ключ участника А: " + a);
        b = BigInteger.valueOf(kx).pow((int) y).mod(BigInteger.valueOf(module));
        System.out.println("Обменный ключ участника В: " + b);
    }
}
