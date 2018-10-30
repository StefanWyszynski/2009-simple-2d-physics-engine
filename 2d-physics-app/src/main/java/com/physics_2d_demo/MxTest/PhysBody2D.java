package com.physics_2d_demo.MxTest;

import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
/**
 * Klasa przechowuje wlasciwosci ciala sztywnego w dwuwymiarowym ukladzie
 * wspolrzednych, Zajmuje się tez reakcją na zderzenie, lub kontakt dwoch
 * obiektow. Symuluje tarcie miedzy obiektami spowalniajac ich predkosc.
 * Jednak nie ma jeszcze zaimplementowanej obslugi obrotow obiektow.
 *
 */
public class PhysBody2D {

    public static final int BODY_TYPE_BOX = 0;
    public static final int BODY_TYPE_OTHER = 1;
    // wspolczynnik tarcia
    private static float fFriction = 0.01f;
    // wspolczynnik odbicia
    private static float fDamping = 0.4f;
    // orientacja w postaci katu obrotu w okolo osi z
    public float rAngle;
    // predkosc obrotu
    public float rAngVelocity;
    // zly sposob na tymczasowe zmienne - tak sie kiedys (za czasow J2ME) pisalo kod :D
    Vec2D vtmp1 = new Vec2D();
    Vec2D vtmp2 = new Vec2D();
    Vec2D vtmp3 = new Vec2D();
    Vec2D vtmp4 = new Vec2D();
    Vec2D vtmp5 = new Vec2D();
    Vec2D vtmp6 = new Vec2D();
    Vec2D vtmp8 = new Vec2D();
    Vec2D vtmp9 = new Vec2D();
    Vec2D vtmp = new Vec2D();
    // klasa wielokatata
    private Poly2D poly;
    // predkosc obiektu fizycznego
    private Vec2D vVel = new Vec2D();
    // pozycja w przestrzeni swiata obiektu fizycznego
    private Vec2D vPos = new Vec2D();
    // masa obiektu fizycznego
    private float fMass;
    // odwrotnosc masy obiektu fizycznego
    private float fInvMass;
    // orientacja ciala
    private Mat2 orientation;

    /**
     * Konstruktor wieloparametrowy tworzacy nowy obiekt fizyczny.
     * Nowym obiekt zbudowany bedzie z punktow reprezentujacych wielokat.
     *
     * @param numVert  liczba wierzcholkow tworzonego wielokata
     * @param aMass    masa obiektu fizycznego
     * @param awidth   szerokosc, lub promien wielokata
     * @param aheight  wysokosc wielokata
     * @param aposx    pozycja w przestrzeni swiata tworzonego obiektu na osi x
     * @param aposy    pozycja w przestrzeni swiata tworzonego obiektu na osi y
     * @param bodyType typ ciala = BODY_TYPE_BOX, lub BODY_TYPE_OTHER
     */
    public PhysBody2D(int numVert, float aMass, float awidth, float aheight,
                      int aposx, int aposy, int bodyType) {
        if (fMass < 0.0001f) {
            fMass = 0.0f;
        }

        vPos.set(aposx, aposy);
        if (bodyType == BODY_TYPE_BOX) {
            poly = Poly2D.BuildBox(awidth, aheight);
        } else {
            poly = Poly2D.BuildBlob(numVert, awidth);
        }
        fMass = aMass;
        fInvMass = (fMass > 0.0000001f) ? 1.0f / fMass : 0.0f;
        rAngle = 0;
        rAngVelocity = 1.0f;
        orientation = new Mat2(rAngle);
    }

    /**
     * zwraca informacje o tym czy obiekt moze sie poruszac
     *
     * @return czy obiekt moze sie poruszac
     */
    public boolean IsUnmovable() {
        return (fMass < 0.0001f);
    }

    /**
     * zwraca pozycje obiektu w przestrzeni swiata
     *
     * @return pozycja obiektu w przestrzeni swiata
     */
    public Vec2D getPos() {
        return vPos;
    }

    /**
     * zwraca predkosc obiektu
     *
     * @return predkosc obiektu
     */
    public Vec2D getVel() {
        return vVel;
    }

    /**
     * Zwraca mase obiektu
     *
     * @return masa obiektu
     */
    public float getMass() {
        return fMass;
    }

    /**
     * Zwraca odwrotnosc masy obiektu (1 / m)
     *
     * @return odwrotnosc masy obiektu
     */
    public float getInvMass() {
        return fInvMass;
    }

    public void transform() {
        if (poly.vertices == null) {
            return;
        }
        for (int i = 0; i < poly.vertices.length; i++) {
            poly.vertices_t[i].set(poly.vertices[i]);
            poly.vertices_t[i].predMultiply(orientation);
            poly.vertices_t[i].add(vPos);
        }
    }

    /**
     * Dodaje sile w postaci wektora do aktualnego akumulatora sil wywyieranych
     * na obiekt fizyczny.
     *
     * @param force sila w postaci wektora
     * @param dt    krok czasowy
     */
    public void addForce(Vec2D force, float dt) {
        if (IsUnmovable()) {
            return;
        }
        force.scale((fInvMass * dt * dt));
        Vec2D.add(force, vVel, vVel);
    }

    /**
     * Odswieza pozycje obiektu na podstawie predkosci
     *
     * @param dt parametr zarezerwowany
     */
    public void update(float dt) {
        if (IsUnmovable()) {
            vVel.set(0, 0);
            return;
        }
        Vec2D.add(vPos, vVel, vPos);

        rAngle += rAngVelocity * dt;
        orientation = new Mat2(rAngle);
    }

    public void render(Canvas canvas, Paint paint) {
        poly.render(canvas, paint);
    }

    /**
     * Sprawdza czy obiekt fizyczny na rzecz ktorego funkcja jest wywolana,
     * koliduje z obiektem podanym jako parametr.
     *
     * @param aBody cialo fizyczne biorace udzial w tescie kolizji
     * @return zwraca true gdy obiekt fizyczny koliduje z obiektem podanym jako
     * parametr
     */
    public boolean Collide(PhysBody2D aBody) {
        if (IsUnmovable() && aBody.IsUnmovable()) {
            return false;
        }

        Vec2D xRelPos = vtmp1;
        Vec2D xRelVel = vtmp2;
        Vec2D.sub(vPos, aBody.vPos, xRelPos);
        Vec2D.sub(vVel, aBody.vVel, xRelVel);

        if (Poly2D.Collide(poly, aBody.poly, xRelPos, xRelVel, 1.0f)) {
            if (Poly2D.collDist < 0.0f) {
                calcOverlap(aBody, Poly2D.collNorm, -Poly2D.collDist);
            } else {
                calcColl(aBody, Poly2D.collNorm);
            }
            return true;
        }
        return false;
    }

    // dwa obiekty koliduja o czasie t, zatrzymaj obiekty po tym czasie
    void calcColl(PhysBody2D xBody, Vec2D N) {
        Vec2D D = vtmp3;
        Vec2D Dn = vtmp4;
        Vec2D Dt = vtmp5;

        D.set(vVel);
        D.sub(xBody.vVel);

        float n = D.dot(N);

        Dn.set(N);
        Dn.scale(n);

        Dt.set(D);
        D.sub(Dn);

        if (n > 0.0f) {
            Dn.set(0, 0);
        }

        float dt = Dt.dot(Dt);
        float friction = fFriction;

        // jesli dt < od kwadratu wspolczynnika przyklejenia obiektow
        if (dt < 0.04f * 0.04f) {
            friction = 1.01f;
        }

        rAngVelocity += -rAngVelocity*friction;

        vtmp8.set(Dn);
        vtmp8.scale(-(1.0f + fDamping));
        vtmp9.set(Dt);
        vtmp9.scale(friction);
        D.set(vtmp8);
        D.sub(vtmp9);

        float m0 = getInvMass();
        float m1 = xBody.getInvMass();
        float m = m0 + m1;
        float r0 = m0 / m;
        float r1 = m1 / m;

        vtmp8.set(D);
        vtmp8.scale(r0);
        vVel.add(vtmp8);

        vtmp8.set(D);
        vtmp8.scale(r1);
        xBody.vVel.sub(vtmp8);
    }

    // dwa obiekty nachodza na siebie. Oddal je o glebokosc penetracji i oblicz
    // kolizje
    void calcOverlap(PhysBody2D xBody, Vec2D xMTD, float overlapDist) {
        vtmp.set(xMTD);
        vtmp.scale(overlapDist);
        if (IsUnmovable()) {
            xBody.vPos.sub(vtmp);
        } else if (xBody.IsUnmovable()) {
            vPos.add(vtmp);
        } else {
            vPos.x += vtmp.x * 0.5f;
            vPos.y += vtmp.y * 0.5f;
            xBody.vPos.x -= vtmp.x * 0.5f;
            xBody.vPos.y -= vtmp.y * 0.5f;
        }

        Vec2D N = vtmp6;
        N.set(xMTD);
        N.normalise();
        calcColl(xBody, N);
    }
}
