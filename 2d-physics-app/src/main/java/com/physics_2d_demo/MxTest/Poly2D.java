package com.physics_2d_demo.MxTest;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
/**
 * Klasa Poly2D przechowuje ksztalt obiektu zbudowanego z wierzcholkow
 * (3 wierzcholki oznacza trojkat), oraz jego wyswietlanie. Zadaniem klasy jest
 * rowniez zwracanie stanu kolizji miedzy dwoma obiektami tego typu.
 * Kolizja wykonana jest za pomoca metody osi separujacych
 * (SAT - ang. Separating Axis Theory)
 */
public class Poly2D {

    // tymczasowy wektor potrzebny w funkcjach (niezbyt optymalne rozwiazanie,
    // ale nadaje sie do testow)
    private static final Vec2D vTemp1 = new Vec2D();
    /*
     * aktualna glebokosc penetracji obiektow jezeli takowa istnieje, w
     * przeciwnym wypadku czas za jaki nastapi kolizja miedzy dwoma obiektami
     */
    public static float collDist;
    /**
     * normalna kolizji, czyli os penetracji dwoch obiektow Poly2D
     */
    public static Vec2D collNorm = new Vec2D();
    // osie separujace scian w postaci wektorow znormalizowanych
    private static Vec2D vAxes[];
    // dlugosc kazdej osi separujacej
    private static float tAxes[];
    // liczba osi separujacych
    private static int axisCnt;
    /*
     * sprawdz czy zakresy obiektow na osi separacji zachodza na siebie,
     * co by oznaczalo ze jest penetracja
     */
    private static BBox box0 = new BBox();
    private static BBox box1 = new BBox();

    static {
        vAxes = new Vec2D[128];
        tAxes = new float[128];
        for (int i = 0; i < 128; i++) {
            vAxes[i] = new Vec2D();
        }
    }

    // wierzcholki obiektu
    public Vec2D vertices[];
    public Vec2D vertices_t[];

    /**
     * Statyczna funkcja, tworzy nowy obiekt klasy Poly2D.
     * Nowym obiektem jest prostokat o zdefiniowanej szerokosci i wysokosci
     * podanej jako parametry.
     *
     * @param width  szerokosc tworzonego prostokata
     * @param height wysokosc tworzonego prostokata.
     * @return nowy obiekt klasy typu Poly2D
     */
    public static Poly2D BuildBox(float width, float height) {
        Poly2D p = new Poly2D();
        p.vertices = new Vec2D[4];
        p.vertices_t = new Vec2D[4];
        p.vertices[0] = new Vec2D(-width / 2, -height / 2);
        p.vertices[1] = new Vec2D(width / 2, -height / 2);
        p.vertices[2] = new Vec2D(width / 2, height / 2);
        p.vertices[3] = new Vec2D(-width / 2, height / 2);
        for (int i = 0; i < 4; i++) {
            p.vertices_t[i] = new Vec2D();
        }
        return p;
    }

    /**
     * Statyczna funkcja, tworzy nowy obiekt klasy Poly2D.
     * Nowym obiekt jest zbudowany z wielokata o promieniu podanym jako drugi
     * parametr i liczbie wierzcholkow podanych jako pierwszy parametr.
     *
     * @param iNumvertices liczba wierzcholkow
     * @param radius       promien wielokata
     * @return nowy obiekt klasy typu Poly2D
     */
    public static Poly2D BuildBlob(int iNumvertices, float radius) {
        Poly2D p = new Poly2D();
        p.vertices = new Vec2D[iNumvertices];
        p.vertices_t = new Vec2D[iNumvertices];

        float a = (float) (Math.PI / iNumvertices);
        float da = (float) ((Math.PI * 2.0f) / iNumvertices);
        for (int i = 0; i < iNumvertices; i++) {
            a += da;
            p.vertices[i] = new Vec2D((float) Math.cos(a) * radius, (float) Math.sin(a) * radius);
        }

        for (int i = 0; i < iNumvertices; i++) {
            p.vertices_t[i] = new Vec2D();
        }
        return p;
    }

    /*
     * Statyczna funkcja zwracajaca rezultat boolowski kolizji miedzy obiektami
     * podanymi jako parametry
     * @param A obiekt pierwszy
     * @param B obiekt drugi
     * @param vRelPos pozycja wzgledna obiektow
     * @param vRelVel predkosc wzgledna obiektow
     * @param dt krok czasowy symulacji fizycznej
     * @return informacja o kolizji, lub jej braku miedzy obiektami z
     * parametrow A, i B
     */
    public static final boolean Collide(Poly2D A, Poly2D B, Vec2D vRelPos,
                                        Vec2D vRelVel, float dt) {
        if (B == null) {
            return false;
        }
        Vec2D E = vTemp1;

        axisCnt = 0;
        float fVel2 = vRelVel.dot(vRelVel);

        if (fVel2 > 0.00001f) {
            vAxes[axisCnt].set(-vRelVel.y, vRelVel.x);
            tAxes[axisCnt] = dt;
            fVel2 = vRelVel.dot(vRelVel);
            if (!IntervalIntersect(A, B, vRelPos, vRelVel, axisCnt, dt)) {
                return false;
            }
            axisCnt++;
        }

        //======================================================================
        // testuj separacje osi obiektu A z obiektem B
        //======================================================================
        for (int j = A.vertices_t.length - 1, i = 0; i < A.vertices_t.length; j = i, i++) {
            Vec2D.sub(A.vertices_t[i], A.vertices_t[j], E);
            vAxes[axisCnt].set(-E.y, E.x);
            if (!IntervalIntersect(A, B, vRelPos, vRelVel, axisCnt, dt)) {
                return false;
            }
            axisCnt++;
        }

        //======================================================================
        // testuj separacje osi obiektu B z obiektem A
        //======================================================================
        for (int j = B.vertices_t.length - 1, i = 0; i < B.vertices_t.length; j = i, i++) {
            Vec2D.sub(B.vertices_t[i], B.vertices_t[j], E);
            vAxes[axisCnt].set(-E.y, E.x);
            if (!IntervalIntersect(A, B, vRelPos, vRelVel, axisCnt, dt)) {
                return false;
            }
            axisCnt++;
        }

        //======================================================================
        // znajdz normalna kolizji i glebokosc penetracji
        //======================================================================
        collDist = 0;
        collNorm.reset();
        if (!FindMTD(axisCnt)) {
            return false;
        }

        //======================================================================
        // upewnij sie ze obiekty zostana od siebie odsuniete a nie przyblizone
        //======================================================================
        if (collNorm.dot(vRelPos) < 0.0f) {
            collNorm.negate();
        }

        return true;
    }

    private static final boolean IntervalIntersect(Poly2D A, Poly2D B,
                                                   Vec2D xOffset, Vec2D vRelVel, int axisInd, float tmax) {

        Vec2D axis = vAxes[axisInd];

        A.GetInterval(axis, box0);
        B.GetInterval(axis, box1);

        float h = Vec2D.dot(xOffset, axis);
        //box0.min += h;
        //box0.max += h;

        float d0 = box0.min - box1.max;
        float d1 = box1.min - box0.max;

        if (d0 > 0.0f || d1 > 0.0f) {
            float v = vRelVel.dot(axis);

            //======================================================================
            // mala predkosc wzgledna, wiec tylko rzuty bedzia mialy znaczenie
            //======================================================================
            if (Math.abs(v) < 0.0000001f) {
                return false;
            }

            float t0 = -d0 / v;
            float t1 = d1 / v;

            if (t0 > t1) {
                float temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tAxes[axisInd] = (t0 > 0.0f) ? t0 : t1;

            if (tAxes[axisInd] < 0.0f || tAxes[axisInd] > tmax) {
                return false;
            }

            return true;
        } else {
            tAxes[axisInd] = (d0 > d1) ? d0 : d1;
            return true;
        }
    }

    /*
     * znajdz os separacji, na ktorej obiekty nachodza na siebie / pokrywaja sie,
     * a wartosc pokrycia obiektow jest mniejsza niz na pozostalych osiach.
     * Funcja zwraca wektor normalny kolizji i glebokosc penetracji
     */
    private static final boolean FindMTD(int iNumAxes) {
        int mini = -1;

        float n = 0.0f;
        // sprawdz czy obiekty beda na siebie nachodzic w nastepnym kroku
        // czasowym. Jezeli tak to zwroc czas kolizji
        for (int i = 0; i < iNumAxes; i++) {
            if (tAxes[i] > 0) {
                if (tAxes[i] > collDist) {
                    mini = i;
                    collDist = tAxes[i];
                    collNorm.set(vAxes[i]);
                    collNorm.normalise();
                }
            }
        }
        if (mini != -1) {
            return true;
        }

        // obiekty na siebie juz nachodza. Oblicz i zwroc glebokosc penetracji
        // oraz wektor penetracji
        for (int i = 0; i < axisCnt; i++) {
            n = vAxes[i].normalise();
            tAxes[i] /= n;

            if (tAxes[i] > collDist || mini == -1) {
                mini = i;
                collDist = tAxes[i];
                collNorm.set(vAxes[i]);
            }
        }

        if (mini == -1) {
            System.out.println("Error\n");
        }
        return (mini != -1);
    }

    private final void GetInterval(Vec2D axis, BBox box) {
        box.min = box.max = vertices_t[0].dot(axis);
        for (int i = 1; i < vertices_t.length; i++) {
            float d = vertices_t[i].dot(axis);
            if (d < box.min) {
                box.min = d;
            } else if (d > box.max) {
                box.max = d;
            }
        }
    }

    public final void render(Canvas g, Paint paint) {
        for (int j = vertices_t.length - 1, i = 0; i < vertices_t.length; j = i, i++) {
            g.drawLine( (vertices_t[j].x),  (vertices_t[j].y),
                     (vertices_t[i].x),  (vertices_t[i].y), paint);
        }
    }

    private static class BBox {

        float min, max;

        BBox(float amin, float amax) {
            min = amin;
            max = amax;
        }

        BBox() {
            min = 0;
            max = 0;
        }
    }
}