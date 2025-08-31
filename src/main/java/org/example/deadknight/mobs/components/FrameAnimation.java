package org.example.deadknight.mobs.components;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс, реализующий покадровую анимацию.
 * <p>
 * Не зависит от FXGL или AnimationTimer. Отвечает только за логику смены кадров.
 * Поддерживает:
 * <ul>
 *     <li>Обновление текущего кадра на основе времени</li>
 *     <li>Сброс анимации к первому кадру</li>
 *     <li>Проверку завершения анимации</li>
 * </ul>
 */
@Getter
@Setter
public class FrameAnimation {

    /**
     * Список кадров анимации.
     */
    private final List<Image> frames;

    /**
     * Время отображения одного кадра (в секундах).
     */
    private final double frameTime;

    /**
     * Индекс текущего кадра.
     */
    private int index = 0;

    /**
     * Накопитель времени для отслеживания смены кадра.
     */
    private double elapsed = 0;

    /**
     * Флаг завершения анимации.
     */
    private boolean finished = false;

    /**
     * Создает анимацию по заданным кадрам и времени показа одного кадра.
     *
     * @param frames    список кадров анимации
     * @param frameTime время отображения одного кадра в секундах
     */
    public FrameAnimation(List<Image> frames, double frameTime) {
        this.frames = frames;
        this.frameTime = frameTime;
    }

    /**
     * Обновляет анимацию на основе прошедшего времени.
     * <p>Если анимация завершена, возвращает текущий кадр без изменений.</p>
     *
     * @param tpf время с последнего обновления (seconds)
     * @return текущий кадр анимации
     */
    public Image update(double tpf) {
        if (finished) return frames.get(index);

        elapsed += tpf;
        if (elapsed >= frameTime) {
            index++;
            elapsed = 0;
            if (index >= frames.size()) {
                index = frames.size() - 1;
                finished = true;
            }
        }
        return frames.get(index);
    }

    /**
     * Сбрасывает анимацию: возвращает к первому кадру и снимает флаг завершения.
     */
    public void reset() {
        index = 0;
        elapsed = 0;
        finished = false;
    }

    /**
     * Проверяет, завершена ли анимация.
     *
     * @return true, если анимация дошла до последнего кадра
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Получает текущий кадр анимации без обновления таймера.
     *
     * @return текущий кадр
     */
    public Image getCurrentFrame() {
        return frames.get(index);
    }
}
