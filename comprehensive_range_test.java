public class comprehensive_range_test {
    // Standalone version of getBonusAmplification for testing
    private static int getBonusAmplification(double hp) {
        int amplifier;
        if (hp <= 65.0) { amplifier = 1; }
        else if (hp <= 100.0) { amplifier = 2; }
        else if (hp <= 150.0) { amplifier = 3; }
        else if (hp <= 200.0) { amplifier = 4; }
        else if (hp <= 450.0) {
            amplifier = Math.max(4, (int) Math.round(4 + (hp - 200.0) / 100.0));
        } else if (hp <= 640.0) {
            amplifier = Math.max(6, (int) Math.round(6 + (hp - 450.0) / 100.0));
            if (hp >= 600) { amplifier -= 1; }
        } else if (hp <= 2000.0) {
            amplifier = Math.max(7, (int) Math.round(7 + (hp - 450.0) / 350.0));
            if (hp >= 640 && hp < 860) { amplifier -= 1; }
            if (hp >= 971 && hp < 980) { amplifier -= 1; }
            if (hp >= 980) { amplifier -= 1; }
            if (hp >= 1321) { amplifier -= 1; }
        } else if(hp <= 3000) {
            int baseAmp = 0;
            amplifier = Math.max(10, (int) Math.round(10 + (hp - 1500.0) / 400.0))+ baseAmp;
        } else {
            int baseAmpAdjust = 0;
            double targetRate = hp / 60.0;
            double requiredAmp = (targetRate / 4.0) - 1.0;
            amplifier = Math.max(0, (int) Math.round(requiredAmp) + baseAmpAdjust);
        }
        amplifier = Math.max(1, amplifier);
        return amplifier;
    }

    private static float calculateHealingAmount(int amplifier) {
        float baseHealing = 1.0f;
        float bonusHealing = amplifier * 1.0f;
        return baseHealing + bonusHealing;
    }

    private static int getTickInterval(int amplifier) {
        int baseInterval = 50;
        int reduction = amplifier * 5;
        return Math.max(5, baseInterval - reduction);
    }

    public static void main(String[] args) {
        System.out.println("HP,Amplifier,HealedPerTick,TickInterval,HeartsPerSecond");
        for (int hp = 40; hp <= 20000; hp += 5) testRow(hp);
        /*
        for (int hp = 40; hp <= 200; hp += 5) testRow(hp);
        for (int hp = 220; hp <= 450; hp += 5) testRow(hp);
        for (int hp = 460; hp <= 2000; hp += 5) testRow(hp);
        for (int hp = 2020; hp <= 5500; hp += 5) testRow(hp);
        for (int hp = 5520; hp <= 7000; hp += 5) testRow(hp);
        for (int hp = 7040; hp <= 20000; hp += 5) testRow(hp);

         */

    }
    private static void testRow(double hp) {
        try {
            int amp = getBonusAmplification(hp);
            float healed = calculateHealingAmount(amp);
            int tickInterval = getTickInterval(amp);
            float heartsPerSecond = healed * (20.0f / tickInterval);
            // Calculate time to heal to full (in seconds)
            float totalHearts = (float)hp;
            float timeToFullHeal = totalHearts / heartsPerSecond;

            // ANSI color codes setup
            String RESET = "\u001B[0m";
            String GREEN = "\u001B[32m";
            String YELLOW = "\u001B[33m";
            String RED = "\u001B[31m";
            String CYAN = "\u001B[36m";
            String color;
            if (timeToFullHeal < 30) color = RED;
            else if (timeToFullHeal < 40) color = YELLOW; // Fast healing
            else if (timeToFullHeal < 80) color = GREEN;
            else if (timeToFullHeal < 120) color = YELLOW;
            else color = RED;

            // Only print if not GREEN
            if (!color.equals(CYAN)) {
                System.out.println(color + "HP - " + hp + ", Amplication - " + amp + ", Healed - " + healed + ", Tick Intervals - " + tickInterval + ", Health Per Second - " + String.format("%.1f", heartsPerSecond) + ", Time to Full Heal (s) - " + String.format("%.2f", timeToFullHeal) + RESET);
            }
        } catch (Exception e) {
            System.out.println("Error at HP=" + hp + ": " + e);
            e.printStackTrace();
        }
    }
}
