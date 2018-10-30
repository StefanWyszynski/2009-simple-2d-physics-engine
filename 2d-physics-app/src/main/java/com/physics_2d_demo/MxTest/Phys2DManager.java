package com.physics_2d_demo.MxTest;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
/**
 * Klasa zarzadza cala symulacja fizyczna w dwuwymiarowym ukladzie wspolrzednych.
 * Tworzy obiekty typu
 * PhysBody2D, przechowuje ich liste, wyswietla, oblicza kolizje miedzy nimi,
 * oraz przesuniecie spowodowane grawitacja i innymi silami niecentralnymi.
 *
 */
public final class Phys2DManager {

    // lista obiektow fizycznych
    private PhysBody2D[] bodies;
    // tymczasowe wektory malo wydajny sposob, ale...
    Vec2D playerForceImp = new Vec2D();
    Vec2D vTemp1 = new Vec2D();

    /**
     * Domyslny konstruktor w tym tescie konstruktor tworzy na sztywno liste
     * 10 obiektow PhysBody2D. Pierwszy obiekt o indeksie 0 jest obiektem gracza.
     * To jest tylko test, Dlatego cztery obiekty z tablicy to sciany o zerowej
     * masie i tym samym nieskonczonej odwrotnosci masy (wiec nieruchome).
     *
     * @param w szerokosc ekranu
     * @param h wysokosc ekranu
     * @see PhysBody2D
     */
    public Phys2DManager(int w, int h) {
        int scrWD = w / 2;
        int scrHD = h / 2;
        int playerW = (w * 5) / 100;
        int playerH = (h * 10) / 100;
        float ObjMaxW = (w * 30) / 100;
        float ObjMaxH = (h * 30) / 100;

        bodies = new PhysBody2D[10];
        //bodies[0] = new PhysBody2D(40, 40, 1500, 15f, 15f);
        bodies[0] = new PhysBody2D(10, 500, (int) (ObjMaxW / 6), 0, 120, 70, PhysBody2D.BODY_TYPE_OTHER);
        // sciany
        // gorna
        bodies[1] = new PhysBody2D(0, 0, w, 10f, scrWD, 0, PhysBody2D.BODY_TYPE_BOX);
        bodies[1].rAngVelocity = 0;

        // dolna
        bodies[2] = new PhysBody2D(0, 0, w, 10f, scrWD, h - 10, PhysBody2D.BODY_TYPE_BOX);
        bodies[1].rAngVelocity = 0;
        // lewa
        bodies[3] = new PhysBody2D(0, 0, 10f, h, 0, scrHD, PhysBody2D.BODY_TYPE_BOX);
        bodies[1].rAngVelocity = 0;
        // prawa
        bodies[4] = new PhysBody2D(0, 0, 10f, h, w, scrHD, PhysBody2D.BODY_TYPE_BOX);
        bodies[1].rAngVelocity = 0;

        bodies[5] = new PhysBody2D(0, 500, ObjMaxW / 3, ObjMaxH / 3, 22, 120, PhysBody2D.BODY_TYPE_BOX);

        bodies[6] = new PhysBody2D(0, 500, ObjMaxW / 2, ObjMaxH / 3, 125, 120, PhysBody2D.BODY_TYPE_BOX);
        bodies[7] = new PhysBody2D(5, 500, (int) (ObjMaxW / 3), 0, 120, 70, PhysBody2D.BODY_TYPE_OTHER);

        bodies[8] = new PhysBody2D(0, 500, ObjMaxW / 4, ObjMaxW / 4, 100, 100, PhysBody2D.BODY_TYPE_BOX);
        bodies[9] = new PhysBody2D(3, 500, (int) (ObjMaxW / 4), 0, 80, 80, PhysBody2D.BODY_TYPE_OTHER);
    }

    /**
     * Przesuwa cialo z tablicy cial z pod inedeksu "polyind, o wartosc
     * przesuniecia x, y
     *
     * @param polyind indeks obiektu fizycznego
     * @param x       przesuniecie obiektu w osi x
     * @param y       przesuniecie obiektu w osi y
     */
    public void movePoly(int polyind, float x, float y) {
        playerForceImp.reset();
        playerForceImp.x = x * bodies[polyind].getMass();
        playerForceImp.y = y * bodies[polyind].getMass();
        vTemp1.set(bodies[polyind].getVel());
        vTemp1.scale(0.3f);
        playerForceImp.sub(vTemp1);
    }

    /**
     * Funkcja wykonuje cala symulacje fizyczna 2d. Oblicza sily powodujace ruch
     * obiektu, nastepnie dla przesunietego obiektu obliczana jest kolizja i
     * korekcja pozycji po kolizji
     *
     * @param dt krok czasowy symulacji
     */
    public void gameUpdate(float dt) {
        //-------------------------------------------------------
        // dodaj siły
        //-------------------------------------------------------
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].addForce(new Vec2D(0, 98.1f * bodies[i].getMass()), dt);
        }

        if (playerForceImp.lenght() > 0.4) {
            bodies[0].addForce(playerForceImp, dt);
            playerForceImp.reset();
        }
        //-------------------------------------------------------
        // odswiez pozycje obiektow
        //-------------------------------------------------------
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].update(dt);
        }

        //-------------------------------------------------------
        // transformuj wierzcholki obiektow do globalnego ukladu
        //-------------------------------------------------------
        try {
            for (int i = 0; i < bodies.length; i++) {
                bodies[i].transform();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //-------------------------------------------------------
        // testuj kolizje
        //-------------------------------------------------------
        for (int i = 0; i < bodies.length; i++) {
            for (int j = 0; j < bodies.length; j++) {
                if (i == j) {
                    continue;
                }
                bodies[i].Collide(bodies[j]);
            }
        }

    }

    /**
     * Rysuje liste obiektow fizycznych
     * @param canvas
     * @param paint
     */
    public void render(Canvas canvas, Paint paint) {
        for (int i = 0; i < bodies.length; i++) {
            bodies[i].render(canvas, paint);
        }
    }
}