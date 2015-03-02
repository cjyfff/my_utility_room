import java.lang.Math;
public class Circle {

    /**
     * @计算pi
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int insideCount = 0;
        int simulateTot = 25000000;
        
        for (int i=0; i<simulateTot; i++){
            double randA = Math.random();
            double randB = Math.random();
            if ((randA * randA + randB * randB) < 1){
                insideCount++;
            }
        }
        System.out.println(insideCount * 1.0 / simulateTot * 4);
    }

}
