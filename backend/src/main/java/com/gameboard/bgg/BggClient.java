package com.gameboard.bgg;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class BggClient {

    private static final String BGG_API = "https://boardgamegeek.com/xmlapi2/thing?id=%s&stats=1";
    private final RestTemplate restTemplate = new RestTemplate();

    private HttpEntity<Void> requestWithHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GameBoard/1.0 (EPITA project; contact@gameboard.local)");
        headers.set("Accept", "application/xml");
        return new HttpEntity<>(headers);
    }

    public Optional<BggGameDto> fetchByBggId(String bggId) {
        try {
            String xml = restTemplate.exchange(
                String.format(BGG_API, bggId), HttpMethod.GET, requestWithHeaders(), String.class
            ).getBody();
            if (xml == null) return Optional.empty();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) return Optional.empty();

            Element item = (Element) items.item(0);
            BggGameDto dto = new BggGameDto();

            NodeList names = item.getElementsByTagName("name");
            for (int i = 0; i < names.getLength(); i++) {
                Element nameEl = (Element) names.item(i);
                if ("primary".equals(nameEl.getAttribute("type"))) {
                    dto.setTitle(nameEl.getAttribute("value"));
                    break;
                }
            }

            NodeList desc = item.getElementsByTagName("description");
            if (desc.getLength() > 0) dto.setDescription(desc.item(0).getTextContent());

            NodeList image = item.getElementsByTagName("image");
            if (image.getLength() > 0) {
                String imgUrl = image.item(0).getTextContent().trim();
                dto.setImageUrl(imgUrl.startsWith("http") ? imgUrl : "https:" + imgUrl);
            }

            NodeList minP = item.getElementsByTagName("minplayers");
            if (minP.getLength() > 0)
                dto.setMinPlayers(Integer.parseInt(((Element) minP.item(0)).getAttribute("value")));

            NodeList maxP = item.getElementsByTagName("maxplayers");
            if (maxP.getLength() > 0)
                dto.setMaxPlayers(Integer.parseInt(((Element) maxP.item(0)).getAttribute("value")));

            NodeList yearEl = item.getElementsByTagName("yearpublished");
            if (yearEl.getLength() > 0)
                dto.setYear(Integer.parseInt(((Element) yearEl.item(0)).getAttribute("value")));

            return Optional.of(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> searchBggId(String title) {
        try {
            String url = "https://boardgamegeek.com/xmlapi2/search?query=" +
                    title.replace(" ", "+") + "&type=boardgame&exact=1";
            String xml = restTemplate.exchange(
                url, HttpMethod.GET, requestWithHeaders(), String.class
            ).getBody();
            if (xml == null) return Optional.empty();

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) return Optional.empty();

            return Optional.of(((Element) items.item(0)).getAttribute("id"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
