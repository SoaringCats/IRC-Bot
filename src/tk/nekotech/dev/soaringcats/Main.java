package tk.nekotech.dev.soaringcats;

import java.util.Scanner;

public class Main extends Thread {
    private static SoaringCats sc;

    public static void main(final String[] args) {
        Main.sc = new SoaringCats();
    }

    @Override
    public void run() {
        final Scanner scan = new Scanner(System.in);
        while (true) {
            final String next = scan.nextLine();
            if (next.equalsIgnoreCase("stop")) {
                Main.sc.quitServer("Stopping! Meow~~");
                Main.sc.dispose();
                try {
                    Thread.sleep(1500); // Sleep for quit to process.
                } catch (final InterruptedException e) {
                }
                System.exit(1);
            } else {
                Main.sc.sendRawLineViaQueue(next);
            }
        }
    }
}
