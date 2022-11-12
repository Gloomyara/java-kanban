public class Converter {

    int calories, distance;//Количество сожжённых килокалорий;Пройденная дистанция (в км).
    Converter(int cal, int dist) {
        calories = cal;
        distance = dist;

    }
    void convert(int stepsSum){
        System.out.println("за месяц вы прошли дистанцию: " + stepsSum*distance/100000 + "км;");
        System.out.println("и сожгли: " + stepsSum*calories/1000 + " килокалорий;");
    }

}
