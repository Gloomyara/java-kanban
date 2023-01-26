package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.task.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class для работы с LocalDateTime в TaskManager преимущественно для Task и SubTask
 */
public class TimeMapManager {
    public static final int MINUTES_CONSTANT = 15; //шаг временной сетки в минутах
    /**
     * Возвращает duration в intMinutes
     */
    public static Function<Task, Integer> getDuration = t1 -> (int) t1.getDuration().toMinutes();
    /**
     * Возвращает true, если есть смысловая нагрузка на таблицу с метками
     */
    public static Predicate<Task> startTimeAndDurationMatters
            = t1 -> t1.getStartTime() == null || getDuration.apply(t1) == 0
            || !t1.getTaskStatus().equals(StatusType.DONE);
    /**
     * Возвращает LocalDateTime, округленное в меньшую сторону по MINUTES_CONSTANT
     */
    public static Function<Task, LocalDateTime> getRoundedStartTime = t1 -> taskTimeRoundDown(t1.getStartTime());
    /**
     * Возвращает LocalDateTime, округленное в большую сторону по MINUTES_CONSTANT
     */
    public static Function<Task, LocalDateTime> getRoundedEndTime = t1 -> taskTimeRoundUp(t1.getEndTime());

    /**
     * Метод для создания временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int minutes, с нижней границей startValue и верхней границей startValue.plusYears(years)
     */
    public static HashMap<LocalDateTime, Boolean> createTimeMap(LocalDateTime startValue, int years) {
        HashMap<LocalDateTime, Boolean> timeMap = new HashMap<>();
        LocalDateTime endValue = startValue.plusYears(years);
        for (LocalDateTime i = startValue; i.isBefore(endValue); i = i.plusMinutes(MINUTES_CONSTANT)) {
            timeMap.put(i, false);
        }
        return timeMap;
    }

    /**
     * Метод для округления в меньшую сторону, времени заданного через метод
     * getEndTime() в Task на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT
     */
    public static LocalDateTime taskTimeRoundUp(LocalDateTime localDateTime) {
        int index = 60 / MINUTES_CONSTANT + 1;
        final int[] utilInstancesArray = new int[index];
        for (int i = 0; i < utilInstancesArray.length; i++) {
            utilInstancesArray[i] = MINUTES_CONSTANT * (i);
        }
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();
        LocalTime roundTime = LocalTime.of(localTime.getHour(), 0);
        int j = localTime.getMinute();
        for (int i : utilInstancesArray) {
            if (j == i) {
                return LocalDateTime.of(localDate, roundTime).plusMinutes(j);
            }
            if (j < i) {
                j = i;
                break;
            }
        }
        return LocalDateTime.of(localDate, roundTime).plusMinutes(j);
    }

    /**
     * Метод для округления в меньшую сторону, времени заданного как startTime в Task
     * на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT
     */
    public static LocalDateTime taskTimeRoundDown(LocalDateTime localDateTime) {
        int index = 60 / MINUTES_CONSTANT + 1;
        final int[] utilInstancesArray = new int[index];
        for (int i = 0; i < utilInstancesArray.length; i++) {
            utilInstancesArray[i] = MINUTES_CONSTANT * (i);
        }
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();
        LocalTime roundTime = LocalTime.of(localTime.getHour(), 0);

        int j = localTime.getMinute();
        for (int i : utilInstancesArray) {
            if (j == i) {
                return LocalDateTime.of(localDate, roundTime).plusMinutes(j);
            }
            if (j < i) {
                j = i;
                break;
            }
        }
        return LocalDateTime.of(localDate, roundTime).plusMinutes(j - MINUTES_CONSTANT);
    }

    /**
     * Метод для коррекции пересечений заданного времени для выполнения Task
     * на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT
     */
    public static void taskStartTimeCorrection(HashMap<LocalDateTime, Boolean> timeMap, Task task) {
        LocalDateTime roundStartTime = getRoundedStartTime.apply(task);
        LocalDateTime roundEndTime = getRoundedEndTime.apply(task);

        if (!timeMap.containsKey(roundStartTime)) {
            throw new NoSuchElementException();
        } else {
            boolean isFree = true;
            while (isFree) {
                boolean b = true;
                for (LocalDateTime i = roundStartTime; i.isBefore(roundEndTime); i = i.plusMinutes(MINUTES_CONSTANT)) {
                    b = timeMap.get(i);
                    if (b) {
                        roundStartTime = i.plusMinutes(MINUTES_CONSTANT);
                        task.setStartTime(roundStartTime);
                        roundEndTime = taskTimeRoundUp(task.getEndTime());
                        break;
                    }
                }
                isFree = b;
            }
        }
    }

    /**
     * Метод для проверки пересечений заданного времени для выполнения Task
     * на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT
     */
    public static boolean taskStartTimeValidation(HashMap<LocalDateTime, Boolean> timeMap,
                                                  Task task) throws NoSuchElementException {
        LocalDateTime roundStartTime = getRoundedStartTime.apply(task);
        LocalDateTime roundEndTime = getRoundedEndTime.apply(task);

        if (!timeMap.containsKey(roundStartTime)) {
            throw new NoSuchElementException("Ошибка! Превышена граница таблицы timeMap");
        } else {
            for (LocalDateTime i = roundStartTime; i.isBefore(roundEndTime); i = i.plusMinutes(MINUTES_CONSTANT)) {
                if (timeMap.get(i)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Метод для добавления временных меток на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT, возвращает HashMap<LocalDateTime, Boolean> tempTimeMap с позитивными
     * метками при отсутствии пересечений
     */
    public static HashMap<LocalDateTime, Boolean> timeMapAddTimeStamps(HashMap<LocalDateTime,
            Boolean> timeMap, Task task) throws TimeStampsCrossingException {
        HashMap<LocalDateTime, Boolean> tempTimeMap = new HashMap<>();
        LocalDateTime start = getRoundedStartTime.apply(task);
        LocalDateTime end = getRoundedEndTime.apply(task);
        //поиск пересечений и добавление меток, если пересечений нет
        if (taskStartTimeValidation(timeMap, task)) {
            for (LocalDateTime i = start; i.isBefore(end); i = i.plusMinutes(MINUTES_CONSTANT)) {
                tempTimeMap.put(i, true);
            }
            return tempTimeMap;
        } else {
            throw new TimeStampsCrossingException("Ошибка! На время С: "
                    + start.format(FileManager.DATE_TIME_FORMATTER)
                    + " до: " + end.format(FileManager.DATE_TIME_FORMATTER)
                    + " уже запланировано выполнение других задач");
        }
    }

    /**
     * Метод для удаления временных меток на основе временной сетки HashMap<LocalDateTime, Boolean> timeMap
     * с шагом int MINUTES_CONSTANT, возвращает HashMap<LocalDateTime, Boolean> tempTimeMap с негативными метками
     */
    public static HashMap<LocalDateTime, Boolean> timeMapRemoveTimeStamps(Task task) {
        HashMap<LocalDateTime, Boolean> tempTimeMap = new HashMap<>();
        LocalDateTime start = getRoundedStartTime.apply(task);
        LocalDateTime end = getRoundedEndTime.apply(task);
        for (LocalDateTime i = start; i.isBefore(end); i = i.plusMinutes(MINUTES_CONSTANT)) {
            tempTimeMap.put(i, false);
        }
        return tempTimeMap;
    }

    /**
     * Метод для добавления/проверки временных меток при обработке задачи,
     * возвращает true, если не найдено пересечений
     */
    public static boolean manageTimeStampsMap(
            HashMap<LocalDateTime, Boolean> timeMap, Task taskNew, Task taskOld)
            throws TimeStampsCrossingException {
        //если в новой есть startTime, duration не равно 0 и задача не завершена,
        //а в старой нет startTime или duration равно 0 или задача завершена
        if (!startTimeAndDurationMatters.test(taskNew) && startTimeAndDurationMatters.test(taskOld)) {
            //добавление меток или исключение при пересечении
            timeMap.putAll(timeMapAddTimeStamps(timeMap, taskNew));
            //если в старой есть startTime, duration не равно 0 и задача не завершена,
            //а в новой нет startTime или duration равно 0 или задача завершена
        } else if (startTimeAndDurationMatters.test(taskNew) && !startTimeAndDurationMatters.test(taskOld)) {
            //удаление старых меток
            timeMap.putAll(timeMapRemoveTimeStamps(taskOld));
            //если значения startTime не равны или значения endTime(duration) не равны
        } else if (!taskNew.getStartTime().equals(taskOld.getStartTime())
                || !taskNew.getEndTime().equals(taskOld.getStartTime())) {
            try {
                //удаление старых меток
                timeMap.putAll(timeMapRemoveTimeStamps(taskOld));
                //добавление новых меток или исключение при пересечении
                timeMap.putAll(timeMapAddTimeStamps(timeMap, taskNew));
            } catch (TimeStampsCrossingException e) {
                //возврат старых меток
                timeMap.putAll(timeMapAddTimeStamps(timeMap, taskOld));
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
