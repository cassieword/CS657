/**
 * Created by xinyu on 3/5/17.
 */
public class Fuzzy {

    public static void main(String[] args) {
        float price;
        float mad;
        String trade;
        int tradeAmount;
        float money;
        float totalMoney = 10000;
        float r = randomWithRange(-1, 1);
        for (int i = 0; i < 150; i++) {
            if(totalMoney > 0) {
                price = getPrice(i,r);
                mad = getMad(i, r);
                trade = ruleBase(fuzzyPrice(price), fuzzyMad(mad));
                tradeAmount = stockTrade(trade);
                money = tradeAmount * price;
                totalMoney += money;
                System.out.println("The current price is: " + price + ", price is " + fuzzyPrice(price));
                System.out.println("The current mad is: " + mad + ", MAD is " + fuzzyMad(mad));
                System.out.println("The trade amount is: " + tradeAmount + " and money is: " + money);
                System.out.println("The total money is: " + totalMoney);
            }
            else
                System.out.println("you have no money");
        }

    }
    private static float getPrice(int i, float r) {
        float p = (float)(10 + 2.5 * Math.sin(Math.PI*2*i/19) + 0.8 * Math.cos(Math.PI*2*i/5) + 7 * r * (i%2));
        return p;
    }
    private static float getMad(int i, float r) {
        float m = (float)(0.5 * Math.cos(0.3*i) - Math.sin(0.3*i) + 0.4 * r * (i%3));
        return m;
    }
    private static String fuzzyPrice(float price) {
        float priceInterval = (float)(20.3 + 0.3)/ 5;
        float p = price + (float)0.3;
        if (p >= 0 && price <= 1 * priceInterval)
            return "VL";
        else if (p > 1 * priceInterval && p <= 2 * priceInterval)
            return "LO";
        else if (p > 2 * priceInterval && p <= 3 * priceInterval)
            return "MD";
        else if (p > 3 * priceInterval  && p <= 4 * priceInterval)
            return "HI";
        else if (p > 4 * priceInterval && p <= 20.6)
            return "VH";
        else
            return "Unexpected";
    }
    private static String fuzzyMad(float mad) {
        float madInterval = (float)4.6 / 3;
        float m = mad + (float)2.3;
        if(m >= 0 && m <= 1 * madInterval)
            return "P";
        else if(m > madInterval && m <= 2 * madInterval)
            return "Z";
        else if(m > 2 * madInterval && m <= 4.6)
            return "N";
        else
            return "Unexpected";
    }
    private static String ruleBase(String price, String mad) {
        if(price.equals("VL") && mad.equals("P"))
            return "BM";
        else if(price.equals("VL") && mad.equals("Z"))
            return "BM";
        else if(price.equals("VL") && mad.equals("N"))
            return "DT";
        else if(price.equals("LO") && mad.equals("P"))
            return "BM";
        else if(price.equals("LO") && mad.equals("Z"))
            return "BF";
        else if(price.equals("LO") && mad.equals("N"))
            return "SF";
        else if(price.equals("MD") && mad.equals("P"))
            return "BF";
        else if(price.equals("MD") && mad.equals("Z"))
            return "DT";
        else if(price.equals("MD") && mad.equals("N"))
            return "SF";
        else if(price.equals("HI") && mad.equals("P"))
            return "BF";
        else if(price.equals("HI") && mad.equals("Z"))
            return "SF";
        else if(price.equals("HI") && mad.equals("N"))
            return "SM";
        else if(price.equals("VH") && mad.equals("P"))
            return "DT";
        else if(price.equals("VH") && mad.equals("Z"))
            return "SM";
        else if(price.equals("VH") && mad.equals("N"))
            return "SM";
        else
            return "Unexpected";
    }
    private static int stockTrade(String trade) {
        int x = 0;
        switch(trade) {
            case "BM":
                x = 800;
                break;
            case "BF":
                x = 400;
                break;
            case "MD":
                x = 0;
                break;
            case "SM":
                x = -800;
                break;
            case "SF":
                x = -400;
                break;
        }
        return x;
    }
    private static float randomWithRange(int min, int max) {
        float range = max - min;
        return (float)(Math.random() * range + min);
    }
}
