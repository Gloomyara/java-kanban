package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.util.exceptions.TimeStampsCrossingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class для работы с LocalDateTime в TaskManager преимущественно для Task и SubTask
 */
public class TimeStampsManager {
    public static final int MINUTES_CONSTANT = 15; //шаг временной сетки в минутах
    /**
     * Возвращает duration в intMinutes
     */
    public static Function<Task, Integer> getDuration = t1 -> (int) t1.getDuration().toMinutes();
    /**
     * Возвращает true, если есть смысловая нагрузка на таблицу с метками
     */
    public static Predicate<Task> startTimeAndDurationMatters
            = t1 -> t1.getStartTime() != null && getDuration.apply(t1) != 0
            && !t1.getTaskStatus().equals(StatusType.DONE);
    /**
     * Возвращает LocalDateTime, округленное в меньшую сторону по MINUTES_CONSTANT
     */
    public static Function<Task, LocalDateTime> getRoundedStartTime = t1 -> taskTimeRoundDown(t1.getStartTime());
    /**
     * Возвращает LocalDateTime, округленное в большую сторону по MINUTES_CONSTANT
     */
    public static Function<Task, LocalDateTime> getRoundedEndTime = t1 -> taskTimeRoundUp(t1.getEndTime());
    final static int index = 60 / MINUTES_CONSTANT + 1;
    final static int[] utilInstancesArray = new int[index];

    /**
     * Метод для создания временной сетки HashSet<LocalDateTime> timeStampsSet
     * с шагом int minutes, с нижней границей startValue и верхней границей startValue.plusYears(years)
     */
    public static HashSet<LocalDateTime> createTimeStampsSet() {
        HashSet<LocalDateTime> timeStampsSet = new HashSet<>();
        for (int i = 0; i < utilInstancesArray.length; i++) {
            utilInstancesArray[i] = MINUTES_CONSTANT * (i);
        }
        return timeStampsSet;
    }

    /**
     * Метод для округления в меньшую сторону, времени заданного через метод
     * getEndTime() в Task на основе временной сетки HashSet<LocalDateTime> timeStampsSet
     * с шагом int MINUTES_CONSTANT
     */
    public static LocalDateTime taskTimeRoundUp(LocalDateTime localDateTime) {

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
     * на основе временной сетки HashSet<LocalDateTime> timeStampsSet
     * с шагом int MINUTES_CONSTANT
     */
    public static LocalDateTime taskTimeRoundDown(LocalDateTime localDateTime) {

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
     * на основе временной сетки HashSet<LocalDateTime> timeStampsSet
     * с шагом int MINUTES_CONSTANT
     */
    public static void taskStartTimeCorrection(HashSet<LocalDateTime> timeStampsSet, Task task) {
        LocalDateTime roundStartTime = getRoundedStartTime.apply(task);
        LocalDateTime roundEndTime = getRoundedEndTime.apply(task);

        boolean isFree = true;
        while (isFree) {
            boolean b = true;
            for (LocalDateTime i = roundStartTime; i.isBefore(roundEndTime); i = i.plusMinutes(MINUTES_CONSTANT)) {
                b = timeStampsSet.contains(i);
                if (b) {
                    roundStartTime = i.plusMinutes(MINUTES_CONSTANT);
                    task.setStartTime(roundStartTime);
                    roundEndTime = getRoundedEndTime.apply(task);
                    break;
                }
            }
            isFree = b;
        }
    }

    /**
     * Метод для проверки пересечений заданного времени для выполнения Task
     * на основе временной сетки HashSet<LocalDateTime> timeStampsSet
     * с шагом int MINUTES_CONSTANT
     */
    public static boolean taskTimeValidation(HashSet<LocalDateTime> timeStampsSet,
                                             Task task) {
        LocalDateTime roundStartTime = getRoundedStartTime.apply(task);
        LocalDateTime roundEndTime = getRoundedEndTime.apply(task);

        for (LocalDateTime i = roundStartTime; i.isBefore(roundEndTime); i = i.plusMinutes(MINUTES_CONSTANT)) {
            if (timeStampsSet.contains(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод для добавления временных меток с шагом int MINUTES_CONSTANT,
     * добавляет метки в HashSet<LocalDateTime> timeStampsSet при отсутствии пересечений
     */
    public static void addTimeStamps(HashSet<LocalDateTime> timeStampsSet, Task task) throws TimeStampsCrossingException {
        LocalDateTime start = getRoundedStartTime.apply(task);
        LocalDateTime end = getRoundedEndTime.apply(task);
        //поиск пересечений и добавление меток, если пересечений нет
        if (taskTimeValidation(timeStampsSet, task)) {
            for (LocalDateTime i = start; i.isBefore(end); i = i.plusMinutes(MINUTES_CONSTANT)) {
                timeStampsSet.add(i);
            }
        } else {
            throw new TimeStampsCrossingException("Ошибка! На время С: "
                    + start.format(FileManager.DATE_TIME_FORMATTER)
                    + " до: " + end.format(FileManager.DATE_TIME_FORMATTER)
                    + " уже запланировано выполнение других задач");
        }
    }

    /**
     * Метод для удаления временных меток из HashSet<LocalDateTime> timeStampsSet с шагом int MINUTES_CONSTANT
     */
    public static void removeTimeStamps(HashSet<LocalDateTime> timeStampsSet, Task task) {
        if (startTimeAndDurationMatters.test(task)) {
            LocalDateTime start = getRoundedStartTime.apply(task);
            LocalDateTime end = getRoundedEndTime.apply(task);
            for (LocalDateTime i = start; i.isBefore(end); i = i.plusMinutes(MINUTES_CONSTANT)) {
                timeStampsSet.remove(i);
            }
        }
    }

    /**
     * Метод для добавления/проверки временных меток при обработке задачи,
     * возвращает true, если не найдено пересечений
     */
    public static boolean manageTimeStampsSet(
            HashSet<LocalDateTime> timeStampsSet, Task taskNew, Task taskOld)
            throws TimeStampsCrossingException {
        //если в новой есть startTime, duration не равно 0 и задача не завершена,
        //а в старой нет startTime или duration равно 0 или задача завершена
        if (startTimeAndDurationMatters.test(taskNew) && !startTimeAndDurationMatters.test(taskOld)) {
            //добавление меток или исключение при пересечении
            addTimeStamps(timeStampsSet, taskNew);
            //если в старой есть startTime, duration не равно 0 и задача не завершена,
            //а в новой нет startTime или duration равно 0 или задача завершена
        } else if (!startTimeAndDurationMatters.test(taskNew) && startTimeAndDurationMatters.test(taskOld)) {
            //удаление старых меток
            removeTimeStamps(timeStampsSet, taskOld);
            //если значения startTime не равны или значения endTime(duration) не равны
        } else if (!taskNew.getStartTime().equals(taskOld.getStartTime())
                || !taskNew.getEndTime().equals(taskOld.getStartTime())) {
            try {
                //удаление старых меток
                removeTimeStamps(timeStampsSet, taskOld);
                //добавление новых меток или исключение при пересечении
                addTimeStamps(timeStampsSet, taskNew);
            } catch (TimeStampsCrossingException e) {
                //возврат старых меток
                addTimeStamps(timeStampsSet, taskOld);
                System.out.println(e.getMessage());
                throw new TimeStampsCrossingException("Не удалось обновить " + taskOld.getTaskType()
                        + " Id: " + taskOld.getTaskId() + "; " + e.getMessage());
            }
        }
        return true;
    }
}
