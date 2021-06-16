# Covid-Booking-Mailer

Underlättar bokning av vaccin-tid i Västra Götaland. Hämtar lediga vaccinationstider och mailar ut enligt satt intervall (15min standard).

Enkelt att justera vilken kommun eller vilka mottagningar man vill få notiser om. 

## Om

* Hämtar lediga tider för Covid-vaccin från Västra Götalands öppna API.
* Mailar ut lediga tider till valda adresser
* Kontrollerar så att det inte skickas fler mail för samma tid
* REST Endpoint för att ta bort och lägga till nya mottagare
* Lagrar mottagare och skickade notiser i minnet
* ~~Twittrar ut nya tider - kommer flyttas till ett separat projekt~~

## Exempel

Exempel på e-post som skickas ut (under testning, ej filtrerat på endast Göteborg). 

![](https://raw.githubusercontent.com/boalbert/covid-vaccine-alert/master/exempel.png?token=AQB62ZI5GEMU7M7UGJNB53DA2MQTE)

## Inställningar

Nedan miljövariabler krävs. Mer info om hur du får tag i dessa finns under resurser.

```
# API
API_URI= 'Länk till API, hittas bland resurser nedan'
CLIENT_ID= 'Erhålles efter ansökan är godkänd'
CLIENT_SECRET= 'Se ovan'

#E-Post
EMAIL_USERNAME= 'Adress du vill skicka ifrån'
EMAIL_PASSWORD= 'Lösenord till e-post du vill skicka ifrån'
EMAIL_FROM= 'Adress som visas som avsändare'
SMTP_HOST= 'SMTP-Server du använder'
SMTP_PORT= 'Port för ovan'
TRANSPORT_STRATEGY= 'SMTP, SMTP_TLS, etc'
```

## Verktyg

* SimpleJavaMail - https://www.simplejavamail.org
* ~~Twitter4j - https://twitter4j.org~~

## Resurser

* API: https://eu1.anypoint.mulesoft.com/exchange/portals/vastra-gotalandsregionen/7022b556-013d-4fc9-966c-298db3fc6a46/e-crm-scheduling-public/

* Tips från VGR: https://vgrblogg.se/utveckling/2021/05/27/hjalp-vgr-testa-vart-api-med-oppna-vaccintider/

* Gmail-inställningar: https://www.simplejavamail.org/features.html#section-gmail

* E-post test/sandbox - under testning: https://mailtrap.io/

