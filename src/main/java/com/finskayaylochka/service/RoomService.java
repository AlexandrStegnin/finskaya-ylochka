package com.finskayaylochka.service;

import com.finskayaylochka.model.*;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.RoomDTO;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import com.finskayaylochka.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomService {

    MoneyService cashService;
    AccountService accountService;
    RoomRepository roomRepository;

//    @Cacheable(Constant.ROOMS_CACHE_KEY)
    public List<Room> findAll() {
        List<Room> rooms = roomRepository.findAll();
        rooms.forEach(room -> {
            Hibernate.initialize(room.getUnderFacility());
            Hibernate.initialize(room.getUnderFacility().getFacility());
        });
        return rooms;
    }

//    @Cacheable(Constant.ROOMS_CACHE_KEY)
    public Room findById(Long id) {
        Room room = roomRepository.findOne(id);
        if (Objects.isNull(room)) {
            throw new EntityNotFoundException("Не найдено помещение");
        }
        return room;
    }

    public Room findByIdWithUnderFacility(Long id) {
        Room room = roomRepository.findOne(id);
        Hibernate.initialize(room.getUnderFacility());
        return room;
    }

//    @Cacheable(Constant.ROOMS_CACHE_KEY)
    public Room findByRoom(String name) {
        return roomRepository.findByName(name);
    }

//    @CacheEvict(value = Constant.ROOMS_CACHE_KEY)
    public ApiResponse deleteById(Long id) {
        List<Money> cashes = cashService.findByRoomId(id);
        cashes.forEach(c -> {
            c.setRoom(null);
            cashService.update(c);
        });
        roomRepository.delete(id);
        accountService.deleteByOwnerId(id, OwnerType.ROOM);
        return new ApiResponse("Помещение успешно удалено");
    }

//    @CachePut(value = Constant.ROOMS_CACHE_KEY, key = "#room.id")
    public ApiResponse update(RoomDTO dto) {
        Room room = new Room(dto);
        roomRepository.saveAndFlush(room);
        return new ApiResponse("Помещение успешно обновлено");
    }

//    @CachePut(Constant.ROOMS_CACHE_KEY)
    public void create(Room room) {
        roomRepository.saveAndFlush(room);
        UnderFacility underFacility = room.getUnderFacility();
        Account account = accountService.findByOwnerId(underFacility.getId(), OwnerType.UNDER_FACILITY);
        int countRooms = roomRepository.countByUnderFacilityId(underFacility.getId());
        accountService.createAccount(room, account, countRooms);
    }

    public ApiResponse create(RoomDTO dto) {
        Room room = new Room(dto);
        roomRepository.save(room);
        return new ApiResponse("Помещение успешно создано");
    }

    public List<Room> init() {
        Room room = new Room();
        room.setId(0L);
        room.setName("Выберите помещение");
        List<Room> roomList = new ArrayList<>(0);
        roomList.add(room);
        roomList.addAll(findAll());
        return roomList;
    }

}
