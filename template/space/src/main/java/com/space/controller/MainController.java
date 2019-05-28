package com.space.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.space.model.Ship;
import com.space.model.ShipDTO;
import com.space.model.ShipType;
import com.space.repository.MainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(value = "/rest")
public class MainController {

    @Autowired
    private MainRepository repository;

    @RequestMapping(value = "/ships", method = GET)
    public ResponseEntity<List<Ship>> getShipList(@RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "planet", required = false) String planet,
                                                  @RequestParam(value = "shipType", required = false) String shipType,
                                                  @RequestParam(value = "after", required = false) Long after,
                                                  @RequestParam(value = "before", required = false) Long before,
                                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                                  @RequestParam(value = "order", required = false) ShipOrder shipOrder,
                                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        List<Ship> ships;

        if (shipOrder != null) {
            switch (shipOrder) {
                case SPEED: {
                    ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.SPEED.getFieldName(), ShipOrder.ID.getFieldName()));
                    break;
                }
                case DATE: {
                    ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.DATE.getFieldName(), ShipOrder.ID.getFieldName()));
                    break;
                }
                case RATING: {
                    ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.RATING.getFieldName(), ShipOrder.ID.getFieldName()));
                    break;
                }
                default: {
                    ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.ID.getFieldName()));
                    break;
                }
            }
        } else {
            ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.ID.getFieldName()));
        }
        if (pageNumber == null) {
            pageNumber = 0;
        }
        if (pageSize == null) {
            pageSize = 3;
        }

        filterShipsList(ships, name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        Map<Integer, List<Ship>> pages = new HashMap<>();

        for (int i = 0; i < ships.size(); i++) {
            int page = i / pageSize;
            List<Ship> pageShips;
            if (pages.containsKey(page)) {
                pageShips = pages.get(page);
            } else {
                pageShips = new ArrayList<>();
                pages.put(page, pageShips);
            }
            pageShips.add(ships.get(i));
        }

        if (pages.containsKey(pageNumber)) {
            return ResponseEntity.ok(pages.get(pageNumber));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/ships/count", method = GET)
    public ResponseEntity<?> getShipsCount(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "planet", required = false) String planet,
                                           @RequestParam(value = "shipType", required = false) String shipType,
                                           @RequestParam(value = "after", required = false) Long after,
                                           @RequestParam(value = "before", required = false) Long before,
                                           @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                           @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                           @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                           @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                           @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                           @RequestParam(value = "minRating", required = false) Double minRating,
                                           @RequestParam(value = "maxRating", required = false) Double maxRating) {

        List<Ship> ships = repository.findAll(new Sort(Sort.Direction.ASC, ShipOrder.ID.getFieldName()));

        filterShipsList(ships, name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return ResponseEntity.ok(ships.size());
    }

    @RequestMapping(value = "/ships", method = POST)
    public ResponseEntity<Ship> createShip(@RequestBody ShipDTO shipDTO) {

        Ship ship = new Ship();

        if (shipDTO.getIsUsed() == null) {
            ship.setIsUsed(false);
        } else {
            ship.setIsUsed(shipDTO.getIsUsed());
        }
        if (shipDTO.getName() == null || shipDTO.getName().length() > 50 || shipDTO.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setName(shipDTO.getName());
        }
        if (shipDTO.getPlanet() == null || shipDTO.getPlanet().length() > 50 || shipDTO.getPlanet().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setPlanet(shipDTO.getPlanet());
        }
        if (shipDTO.getSpeed() == null || shipDTO.getSpeed() < 0.01 || shipDTO.getSpeed() > 0.99) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setSpeed(shipDTO.getSpeed());
        }
        if (shipDTO.getCrewSize() == null || shipDTO.getCrewSize() < 1 || shipDTO.getCrewSize() > 9999) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setCrewSize(shipDTO.getCrewSize());
        }
        if (shipDTO.getProdDate() == null || shipDTO.getProdDate() < 0) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setProdDate(new Date(shipDTO.getProdDate()));
        }
        if (shipDTO.getShipType() == null || shipDTO.getShipType().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            ship.setShipType(ShipType.valueOf(shipDTO.getShipType()));
        }

        repository.save(ship);

        return ResponseEntity.ok(ship);
    }

    @RequestMapping(value = "/ships/{id}", method = GET)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {

        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Ship> optionalShip = repository.findById(id);
        Ship ship;
        if (optionalShip.isPresent()) {
            ship = optionalShip.get();
        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ship);
    }

    @RequestMapping(value = "/ships/{id}", method = POST)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody ShipDTO shipDTO) {

        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Ship> optionalShip = repository.findById(id);

        if (!optionalShip.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Ship ship = optionalShip.get();
        if (shipDTO.getName() != null) {
            if (shipDTO.getName().isEmpty() || shipDTO.getName().length() > 50) {
                return ResponseEntity.badRequest().build();
            } else {
                ship.setName(shipDTO.getName());
            }
        }
        if (shipDTO.getPlanet() != null) {
            if (shipDTO.getPlanet().isEmpty() || shipDTO.getPlanet().length() > 50) {
                return ResponseEntity.badRequest().build();
            } else {
                ship.setPlanet(shipDTO.getPlanet());
            }
        }
        if (shipDTO.getShipType() != null) {
            ship.setShipType(ShipType.valueOf(shipDTO.getShipType()));
        }
        if (shipDTO.getProdDate() != null) {
            if (shipDTO.getProdDate() < 0) {
                return ResponseEntity.badRequest().build();
            } else {
                ship.setProdDate(new Date(shipDTO.getProdDate()));
            }
        }
        if (shipDTO.getIsUsed() != null) {
            ship.setIsUsed(shipDTO.getIsUsed());
        }
        if (shipDTO.getSpeed() != null) {
            if (shipDTO.getSpeed() < 0.01 || shipDTO.getSpeed() > 0.99) {
                return ResponseEntity.badRequest().build();
            } else {
                ship.setSpeed(shipDTO.getSpeed());
            }
        }
        if (shipDTO.getCrewSize() != null) {
            if (shipDTO.getCrewSize() < 1 || shipDTO.getCrewSize() > 9999) {
                return ResponseEntity.badRequest().build();
            } else {
                ship.setCrewSize(shipDTO.getCrewSize());
            }
        }

        repository.save(ship);

        return ResponseEntity.ok(ship);
    }

    @RequestMapping(value = "/ships/{id}", method = DELETE)
    public ResponseEntity<Void> deleteShip(@PathVariable("id") Long id) {

        if (id == null || id < 1) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Ship> optionalShip = repository.findById(id);

        if (!optionalShip.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    private void filterShipsList(List<Ship> ships, String name, String planet, String shipType,
                                 Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed,
                                 Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        if (name != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (!ships.get(i).getName().contains(name)) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (planet != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (!ships.get(i).getPlanet().contains(planet)) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (shipType != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (!ships.get(i).getShipType().equals(ShipType.valueOf(shipType))) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (after != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getProdDate().getTime() < after) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (before != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getProdDate().getTime() > before) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (minSpeed != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getSpeed() < minSpeed) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (maxSpeed != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getSpeed() > maxSpeed) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (minCrewSize != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getCrewSize() < minCrewSize) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (maxCrewSize != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getCrewSize() > maxCrewSize) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (minRating != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getRating() < minRating) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (maxRating != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (ships.get(i).getRating() > maxRating) {
                    ships.remove(i);
                    i--;
                }
            }
        }
        if (isUsed != null) {
            for (int i = 0; i < ships.size(); i++) {
                if (!ships.get(i).getIsUsed().equals(isUsed)) {
                    ships.remove(i);
                    i--;
                }
            }
        }
    }
}
