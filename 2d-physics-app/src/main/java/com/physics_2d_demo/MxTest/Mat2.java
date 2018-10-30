package com.physics_2d_demo.MxTest;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
/**
 * Matrix 2 x 2.
 */
public class Mat2 {

    float m11, m12, m21, m22;

    /**
     * Konstruktor ustawiajacy elementy macierzy na podstawie przekazanych
     * parametrow.
     * Inaczej: konstruktor kopiujacy.
     */
    public Mat2(float _m11, float _m12, float _m21, float _m22) {
        m11 = _m11;
        m12 = _m12;
        m21 = _m21;
        m22 = _m22;
    }

    /**
     * Konstruktor ustawiajacy elementy macierzy na podstawie przekazanej macierzy.
     * Inaczej: konstruktor kopiujacy.
     */
    public Mat2(Mat2 m) {
        m11 = m.m11;
        m12 = m.m12;
        m21 = m.m21;
        m22 = m.m22;
    }

    /**
     * Domyslny konstruktor zerujacy elementy macierzy
     */
    public Mat2() {
        m11 = m12 = m21 = m22 = 0;
    }

    /**
     * Konstruktor jednoparametrowy, tworzacy macierz obrotu. Ustawia macierz
     * obrotu w okolo osi z, o kat podany jako parametr angle (w radianach)
     * 1 radian = PI / 180
     *
     * @param angle kat w radianach
     */
    public Mat2(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        m11 = m22 = c;
        m12 = s;
        m21 = -s;
    }

    /**
     * Ustawia macierz identycznosciawa (elementy na przekatej rowne 1)
     */
    public final void setIdentity() {
        m11 = m22 = 1.0f;
        m21 = m12 = 0.0f;
    }

    /**
     * Ustawia macierz zerowa (elementy rowne 0)
     */
    public final void getZero() {
        m11 = m21 = m12 = m22 = 0.0f;
    }

    /**
     * Transponuje macierz
     */
    public final void tranpose() {
        float tm21 = m12;
        float tm12 = m21;
        m21 = tm21;
        m12 = tm12;
    }

    /**
     * Mnozy macierz, przez macierz z parametru M, w nastepujacej kolejnosci:
     * R *= M;
     *
     * @param M macierz
     */
    public void getPostMultipledBy(Mat2 M) {
        float tm11 = m11 * M.m11 + m12 * M.m21;
        float tm21 = m21 * M.m11 + m22 * M.m21;
        float tm12 = m11 * M.m12 + m12 * M.m22;
        float tm22 = m21 * M.m12 + m22 * M.m22;
        m11 = tm11;
        m21 = tm21;
        m12 = tm12;
        m22 = tm22;
    }

    /**
     * Mnozy macierz, przez macierz z parametru M, w nastepujacej kolejnosci:
     * R = M * R;
     *
     * @param M macierz
     */
    public void getPredMultipledBy(Mat2 M) {
        float tm11 = m11 * M.m11 + m12 * M.m12;
        float tm21 = m21 * M.m11 + m22 * M.m12;
        float tm12 = m11 * M.m21 + m12 * M.m22;
        float tm22 = m21 * M.m21 + m22 * M.m22;
        m11 = tm11;
        m21 = tm21;
        m12 = tm12;
        m22 = tm22;
    }

    /**
     * Skaluje macierz przez parametr ascale
     *
     * @param ascale wartosc przez ktora elementy macierzy zostana pomnozone
     */
    public void getScaledBy(float ascale) {
        m11 = m11 * ascale;
        m21 = m21 * ascale;
        m12 = m12 * ascale;
        m22 = m22 * ascale;
    }
}
