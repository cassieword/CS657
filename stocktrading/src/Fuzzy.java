/**
 * Created by xinyu on 3/5/17.
 */
public class Fuzzy {

    public static void main(String[] args) {
        float price;
        float mad;
        float money;
        float stock = 0;
        float totalMoney = 10000;
        float r = randomWithRange(-1, 1);
        for(int i = 0; i < 150; i++) {
            price = getPrice(i,r);
            mad = getMAD(i,r);
            float priceArray[] = fuzzyPrice(price);
            float madArray[] = fuzzyMAD(mad);
            float ruleBase[][] = ruleBase(priceArray, madArray);
            float cog = cogOutput(ruleBase);
            //cog negtive sell, cog positive buy
//            if(cog < 0 && stock < Math.abs(stock)) {
//                cog = -stock;
//            }
            money = calculate(cog,price);
            totalMoney = totalMoney - money;
//            stock = stock + cog;
            if(totalMoney < 0){
                System.out.println("Your money is not enough! Please wait to next trade.");
                totalMoney = totalMoney + money;
//                stock = stock - cog;
                i++;
            }
            System.out.println("For the " + i +"th day, the money you have is " + totalMoney + ", the stocks you have is "+ stock);
        }
    }
    private static float getPrice(int i, float r) {
        float p = (float)(10 + 2.5 * Math.sin(Math.PI*2*i/19) + 0.8 * Math.cos(Math.PI*2*i/5) + 7 * r * (i%2));
        return p;
    }
    private static float getMAD(int i, float r) {
        float m = (float)(0.5 * Math.cos(0.3*i) - Math.sin(0.3*i) + 0.4 * r * (i%3));
        return m;
    }

    private static float[] fuzzyPrice(float price) {
        // priceArray {'VL','LO','MD','HI','VH'}
        float priceArray[];
        priceArray = new float[5];
        if (price <= 5)
            priceArray[0] = 1;
        else if (price > 5 && price <= 7) {
            priceArray[0] = (float) (-0.5 * price + 3.5);
            priceArray[1] = (float) (0.5 * price - 2.5);
        } else if (price > 7 && price <= 9) {
            priceArray[1] = (float) (-0.5 * price + 4.5);
            priceArray[2] = (float) (0.5 * price - 3.5);
        } else if (price > 9 && price <= 11) {
            priceArray[2] = (float) (-0.5 * price + 5.5);
            priceArray[3] = (float) (0.5 * price - 4.5);
        } else if (price > 11 && price <= 13) {
            priceArray[3] = (float) (-0.5 * price + 6.5);
            priceArray[4] = (float) (0.5 * price - 5.5);
        } else {
            priceArray[4] = 1;
        }
        return priceArray;
    }

    private static float[] fuzzyMAD(float mad) {
        //madArray {'N','Z','P'}
        float madArray[];
        madArray = new float[3];
        if(mad <= -1)
            madArray[0] = 1;
        else if (mad > -1 && mad <= -0.5) {
            madArray[0] = - mad;
    }
        else if (mad > -0.5 && mad <= 0) {
            madArray[0] = - mad;
            madArray[1] = 2 * mad + 1;
        }
        else if (mad > 0 && mad <= 0.5) {
            madArray[1] = -2 * mad + 1;
            madArray[2] = mad;
        }
        else if (mad > 0.5 && mad <= 1) {
            madArray[2] = mad;
        } else {
            madArray[2] = 1;
        }
        return madArray;
    }
    private static float[][] ruleBase(float p[], float m[]) {
        //rule base table { row: MAD fuzzy set, col: price fuzzy set}
        float ruleTable[][];
        ruleTable = new float[3][5];
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < 3; j++) {
                ruleTable[j][i] = Math.min(p[i],m[j]);
            }
        }
        return ruleTable;
    }
    private static float cogOutput(float t[][]) {
        //trade decision {SM:-800 ~ -200},{SF:-400 ~ 0},{DT:-200 ~ 200}
        //{BF:0 ~ 400}, {BM:200 ~ 800}
        float sm = Max(t[0][3],t[0][4],t[1][4]);
        float sf = Max(t[0][1],t[0][2],t[1][3]);
        float dt = Max(t[0][0],t[1][2],t[2][4]);
        float bf = Max(t[1][1],t[2][2],t[2][3]);
        float bm = Max(t[1][0],t[2][0],t[2][1]);
        float cogX = (-800-700-600-500-400-300-200)*sm + (-400-300-200-100)*sf + (-200-100+100+200)*dt + (100+200+300+400)*bf + (200+300+400+500+600+700+800)*bm;
        float cogY = sm*7 + sf*4 + dt*4 + bf*4 + bm*7;
        float cog = (float)Math.ceil(cogX/cogY);
        return cog;
    }
    private static float randomWithRange(int min, int max) {
        float range = max - min;
        return (float)(Math.random() * range + min);
    }
    private static float Max(float x, float y, float z) {
        float max = Math.max(Math.max(x,y),z);
        return max;
    }
    private static float calculate(float cog, float p) {
        float m;
        m = cog * p;
        if(m < -800) m = -800;//sell
        else if(m > 800) m = 800;//buy
        else m = m;
        return m;
    }
}
