# Házi feladat specifikáció

## Mobil- és webes szoftverek
### 2023. 10. 22.
### Simple Inventory
### Tóth Gábor Tamás - HAFLCT
### toth.gabor.tamas@edu.bme.hu
### Laborvezető: Kövesdán Gábor


## Bemutatás

Az alkalmazás egy leltározó alkalmazás, amivel könnyedén nyilvántarthatja egy ember vagy akár egy kisvállalkozás a különböző fizikai eszközeit, kiegészítőit. A felhasználónak lehetősége van tételtípusokat létrehozni, és tétel bejegyzéseket létrehozni, ezenkívül vonalkódot generálni tételekhez és azt kamerával beolvasni a beazonosítás érdekében. Az adatok egy központi adatbázisban lesznek tárolva, ezáltal több felhasználó is eléri őket a telefonjára telepített alkalmazáson keresztül.

Az alkalmazás ötlete onnan jött, hogy több lakhelyem is van, a lakhelyeimen rengeteg cuccom van nekem/a családnak, és általában nehéz megtalálni dolgokat, vagy egyáltalán megállapítani, hogy van-e az adott típusú dologból valahol (pl. Displayport kábelt keresek, és nem tudom, van-e, illetve hol). Ez az alkalmazás megoldást adna ezekre a problémákra mind nekem, mind a családtagjaim számára, és később akár más használati esetekre is jó lesz.


## Főbb funkciók

Az alkalmazás adatai egy központi adatbázisban lesznek tárolva.

### Főképernyő
Indításkor az **alkalmazás főképernyője** látható. Amennyiben még nem jelentkezett be vagy nem regisztrált a felhasználó, egy **login screen** látható a főképernyő megjelenítése előtt, ahol ezt meg lehet tenni.

A főképernyőn a felhasználó **kedvenc tételei, tétel típusai** vannak listázva, amire kattintva megjelenik a **tétel/tételtípus részletei** nézet.

**Keresőmező** segítségével gyorsan lehet keresni az összes rögzített tétel és tétel típus között. Ezenkívül egyéb gyors műveleteket is lehet végezni: **új tétel rögzítését**, illetve **vonalkód beolvasást**.

A főképernyőről tovább lehet navigálni a **tétel típusok** vagy a **tételek** nézetre.


### Tétel típusok
Ebben a nézetben tétel típusokon lehet CRUD műveleteket végezni. A tétel típusok **hierarchiában** vannak, minden tétel típusnak lehet **egy szülője** (kategória), illetve **bármennyi gyereke**.

Listázva vannak azok a tétel típusok, amiknek nincs szülője (gyökér kategóriák), melyeket lehet **szerkeszteni**, **törölni**, **hozzáadni a kedvencekhez**. Törölni és kedvencekhez hozzáadni egyszerre több elemet is lehet, jelölőnégyzettel való kijelöléssel. Ezenkívül lehet **új tétel típust létrehozni**.

Tétel típus szerkesztésekor, vagy egy új létrehozásakor a **tétel típus részletek** nézet nyílik meg.


### Tétel típus részletek
Ebben a nézetben be lehet állítani a tétel típus **nevét**, **kategóriáját** (ez egy másik tétel típus példány).

Ebben a nézetben el lehet **nagiválni a szülő típus részleteire** (amennyiben van szülője), illetve listázva vannak a **gyerek tétel típusok, ezek részleteire is el lehet navigálni**. A listázás funkcionálisan megegyezik a tétel típusok nézetben található listázással.

Minden tételnek be lehet állítani a tétel típusát. A tétel típusok részletek nézetben listázva vannak azok a tételek, amiknek az éppen megjelenített tétel típus a típusa. Ezeknek **a tételeknek a részleteire is el lehet navigálni**. A listázás funkcionálisan megegyezik a tételek nézetben található listázással.

**Példa tétel típusok:**

Audio-video kábelek
- Név: A/V kábel
- Kategória: Kábel
- Altípusok: HDMI kábel, DP kábel, VGA kábel, 3xRCA M-M összekötő, ...
- Tételek: ismeretlen av kábel #1, ...

HDMI kábelek
- Név: HDMI kábel
- Kategória: A/V kábel
- Altípusok: -
- Tételek: HDMI kábel #1, HDMI kábel 3m, ...


### Tételek, tétel részletek
Ezek a nézetek nagyon hasonlóak a tétel típus/tétel típus részletek nézethez. A tételeken lehet ugyanazokat a műveleteket végezni, a tételek is ugyanolyan hierarchiában vannak. Azokat a tételeket, melyeknek vannak gyerek tételei, összetett tételnek nevezzük, ilyen lehet pl. egy doboz, egy polc, stb.

**A különbségek:**
- Tételek nézetben lehet 1 vagy akár több tételhez **vonalkódot generálni**, ami egy **pdf fájlt exportál** a kiválasztott tételek vonalkódjával, ennek célja, hogy a felhasználó kinyomtassa és ráragassza az adott tételekre.
- Tételek nézetben lehet **vonalkódot beolvasni** egy termék beazonosítása érdekében.
- Tétel részletek nézetben be lehet állítani a tételnek a **nevét**, **tétel típusát**, **szülő tételét**, és hogy az adott tétel egy összetett tétel-e.
- Tétel részletek nézetben meg lehet tekinteni a tétel **vonalkódját**.
- Tétel részletek nézetben **listázva vannak a gyerek tételek**, amennyiben az adott tétel egy összetett tétel, és **el lehet navigálni a szülő tétel részleteire** illetve **a gyerek tételek részleteire**.

**Példa tételek:**

Doboz
- Név: Doboz #1
- Típus: Doboz
- Összetett: igen
- Szülő tétel: -
- Altételek: HDMI kábel #1, HDMI kábel 3m, DP kábel 5m

DP kábel
- Név: DP kábel 5m
- Típus: DP kábel
- Összetett: nem
- Szülő tétel: Doboz #1

## Választott technológiák:

- UI
- Fragmentek
- RecyclerView
- Perzisztens adattárolás: Firebase


# Házi feladat dokumentáció (ha nincs, ez a fejezet törölhető)