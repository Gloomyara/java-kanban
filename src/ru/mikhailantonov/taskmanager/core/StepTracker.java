public class StepTracker {
    Converter converter = new Converter(50, 75); //Объявляем конвертер
    MonthData[] monthToData; //придаем классу свойства массива

    //объявляем массив для лимита индексов
    int[] monthMaxDay = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    //объявляем массив String месяцев для вывода
    String[] monthString = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    int stepsGoal = 10000;
    boolean check = true; //проверка ввода
    String back = "Возврат в главное меню.";

    public StepTracker() {

        monthToData = new MonthData[12];

        for (int i = 0; i < monthToData.length; i++) {
            monthToData[i] = new MonthData();
            monthToData[i].stepsArray = new int[monthMaxDay[i]]; //задаем лимит индексов массивам дней
        }
    }


    class MonthData { //Idea выдает предупреждение Inner class 'MonthData' may be 'static'
        int[] stepsArray;

        int saveSteps(int day, int steps) {
            stepsArray[day - 1] = steps;
            return stepsArray[day - 1];
        }
    }

    void printStepsStat(int month) { //Количество пройденных шагов по дням

        int i = month - 1, stepsMax = 0, sum = 0;

        System.out.println("Статистика за " + monthString[i] + ":");

        for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
            System.out.print((j + 1) + " день:" + monthToData[i].stepsArray[j] + ",   ");

            sum = sum + monthToData[i].stepsArray[j];

            if (stepsMax < monthToData[i].stepsArray[j]) {
                stepsMax = monthToData[i].stepsArray[j];
            }
        }

        System.out.println(" ");
        System.out.println("в этом месяце вы сделали: " + sum + " шагов;");
        System.out.println("максимальное количество шагов за день: " + stepsMax + ";");
        System.out.println("за день в среднем: " + sum / monthToData[i].stepsArray.length + " шагов;");
        converter.convert(sum); // вызываем метод класса Converter
    }

    void stepsGoal(int newStepsGoal) {

        if (newStepsGoal < 0) {
            System.out.println("Значение не может быть отрицательным.");
            System.out.println("Возврат в главное меню.");
        } else {
            stepsGoal = newStepsGoal;
        }
    }

    void bestSeries(int month) { //из-за лимита дней при (j<monthToData[i].stepsArray.length) серию считает не корректно

        int i = month - 1, monthSeriesMax = 0, monthSeries = 0;
        int yearSeriesMax = 0, yearSeries = 0, saveMonth = 0;

        for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
            if (monthToData[i].stepsArray[j] >= stepsGoal) {
                monthSeries++;
            }
            if (monthSeriesMax < monthSeries) {
                monthSeriesMax = monthSeries;
            } else {   //if (monthToData[i].stepsArray[j] < stepsGoal)
                monthSeries = 0;
            }
        }
        System.out.println("Лучшая серия за месяц: " + monthSeriesMax + " дней.");

        for (i = 0; i < monthToData.length; i++) { //Лучшая серия
            for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
                if (monthToData[i].stepsArray[j] >= stepsGoal) {
                    yearSeries++;
                }
                if (yearSeriesMax < yearSeries) {
                    yearSeriesMax = yearSeries;
                    saveMonth = i;

                } else {   //if (monthToData[i].stepsArray[j] < stepsGoal)
                    yearSeries = 0;
                }
            }
        }
        System.out.println("Cерия за " + monthString[saveMonth] + " на данный момент лучшая за год, - " + yearSeriesMax + " дней. ");
    }

    void monthCheck(int month) { //проверка месяцев

        if (month == 0) {
            System.out.println(back);
            check = false;
        } else if (month > 12) {
            System.out.println("Привет, инопланетянам! На Земле в году 12 месяцев");
            System.out.println(back);
            check = false;
        } else if (month < 0) {
            System.out.println("Привет из будущего! Значение месяца не может быть отрицательным");
            System.out.println(back);
            check = false;
        } else {
            check = true;
        }
    }

    void dayCheck(int month, int day) { //проверка дней

        int i = month - 1;

        if (day == 0) {
            System.out.println(back);
            check = false;
        } else if (day < 0) {
            System.out.println("Ожидалось число от 1 до " + monthToData[i].stepsArray.length);
            System.out.println(back);
            check = false;
        } else if (day > monthToData[i].stepsArray.length) {
            System.out.println("Ожидалось число от 1 до " + monthToData[i].stepsArray.length);
            System.out.println(back);
            check = false;
        } else {
            check = true;
        }
    }

    void stepsOutput(int month, int day, int steps) { //вывод и проверка шагов
        int i = month - 1;

        if (steps < 0) {
            System.out.println("Значение не может быть отрицательным.");
            System.out.println(back);
            check = false;
        } else {
            check = true;
            System.out.println("Новое значение за " + monthString[i] + " " + day + ": " + monthToData[i].saveSteps(day, steps));
        }
    }
}

/*
если декомпозировать еще больше:
    String monthStepsMax(int month) { //Максимальное пройденное количество шагов в месяце;
        int i = month - 1;
        int stepsMax = 0;
        for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
            if (stepsMax < monthToData[i].stepsArray[j]) {
                stepsMax = monthToData[i].stepsArray[j];
            }
        }
        return ("максимальное количество шагов за день: " + stepsMax + ";");
    }
    String averageSteps(int month) {
        int i = month - 1;
        int sum = 0;
        for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
            sum = sum + monthToData[i].stepsArray[j];
            System.out.println("за день в среднем: " + sum / monthMaxDay[month - 1] + " шагов;");
        }
    }
    int stepsSum(int month) { //Сумма шагов за месяц;
        int i = month - 1;
        int sum = 0;
        for (int j = 0; j < monthToData[i].stepsArray.length; j++) {
            sum = sum + monthToData[i].stepsArray[j];
        }
        return sum;
    }
 */
