# JoyConConnect

Jednoduchá Android aplikace pro kontrolu, zda tablet rozpoznal levý a pravý Nintendo Joy-Con přes Bluetooth.

## Co aplikace umí
- otevřít systémové Bluetooth nastavení,
- zobrazit Androidem rozpoznané gamepady/joysticky,
- zobrazit stisky tlačítek a pohyb analogových os, pokud je Android předává aplikaci,
- zobrazit vizuální náhled dotykových tlačítek.

**Omezení:** dotykový náhled není systémový virtuální gamepad a nepředává stisky do jiných her. Android to běžné aplikaci bez speciální systémové podpory/opravení nedovoluje. Také nelze zaručit, že Android spojí oba Joy-Cony do jednoho ovladače; záleží na verzi systému a hře.

## Sestavení APK bez počítače přes GitHub
1. Nahraj obsah tohoto ZIPu do kořenové složky repozitáře `JoyConConnect` (ne samotnou složku `JoyConConnect`).
2. Otevři kartu **Actions** v repozitáři.
3. Vyber **Build Joy-Con Connect APK**. Po nahrání souborů se sestavení spustí automaticky; případně klepni na **Run workflow**.
4. Po úspěšném dokončení otevři daný běh workflow a v části **Artifacts** stáhni `JoyConConnect-debug-apk`.
5. Rozbal stažený ZIP a nainstaluj `app-debug.apk`. Android může požádat o povolení instalace z tohoto zdroje.

## Párování
1. V aplikaci klepni na „Otevřít Bluetooth nastavení“.
2. Na Joy-Conu podrž malé tlačítko SYNC na boku, dokud nezačnou blikat kontrolky.
3. V seznamu Bluetooth vyber Joy-Con (L), potvrď párování.
4. Stejně spáruj Joy-Con (R).
5. Vrať se do aplikace a klepni na „Obnovit seznam ovladačů“.

Pokud se ovladače zobrazí, zkus tlačítka a páčky. Některé hry nemusí podporovat Joy-Cony nebo samostatné levý/pravý ovladač.
