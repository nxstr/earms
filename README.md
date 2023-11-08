# EAR Semestral Meeting Scheduler



## Učel aplikace

Aplikace je určena hlavně pro lidi, které chtějí se dohodnout na nějaký termín události. Díky použiti takové aplikace lidi neztratí hlasovaní třeba mezi zprávy v messengeru, kde trvá diskuze ohledné teto 
události.
Aplikace bude vhodná pro lidi, které nemají hodně volného casu ale  chtějí se sjednat s kamarády.


## Základní vlastnosti aplikace

### Registrace uživatelů
Pro registrace uživatel musí uvést jméno, příjmení, email, uživatelské jméno a heslo. 

### Vytvořeni události
Po registrace může založit novou událost, kde má uvést název, datum  dokdy bude událost otevřena (OpenDueTo) k hlasovaní, připadne místo konaní události a popis události. U každé události musí byt alespoň jeden datum, za který bude možné hlasovat. Pokud majitel události neukáže žádný datum, zgeneruje se automaticky (po vytvořeni dalších
datumu bude možné tento odstranit).

### Přidáni guestu k události
Majitel události může přidat k události guesty, které mohou hlasovat u jednotlivých hlasu. Guest dostane pozvánku emailem a musí si založit účet v aplikaci, po přihlášeni může hlasovat.

### Hlasovaní
Kazdy uživatel, který má povoleni k hlasovaní u jednotlivé události (guest) může označit každý nabídnuty datum pro budoucí událost. Existuje 3 typy hlasu: POSITIVE(termín vyhovuje), NEGATIVE(termín
nevyhovuje), NEUTRAL(nejsem si jisty). Hlas se da měnit dokud událost nebude označena jako uzavřena (počítá se jako uzavřena po expirace datumu openDueTo). U hlasu je možné přidat komentář.

### Vyber datumu události
Majitel události má moznost vybrat datum provedeni události ještě před tím než se událost uzavři.

### Uzavřeni události
Událost automaticky se uzavři po expiraci datumu openDueTo. V případě, jestli nebyl označen datum provedeni události majitelem, systém automaticky označí isFinal datum, který má největší počet pozitivních hlasu.


## Omezeni systému

Předpokládá se, že majitel události technicky hlasovat jako guesty nebude, protože tím, jaké datumy jsou představené k hlasovaní už svůj výběr označil. Zároveň to platí proto, ze jednotlivé datumy může
odstraňovat a přidávat.
Při implementace aplikace nepředpokládá se uloženi žádných šablon události, který by byl vhodný pro periodické události
