package learn.design.patterns.simple.factory;

/**
 * 客户端类
 */
public class Client {
    public static void main(String[] args) {
        Chart chart = ChartFactory.getChart("pie");
        chart.display();
    }
}
