package moo.diarystorage.service;

import java.util.List;

public interface IDiaryStorageService {

    public void insertOrUpdate(Diary one);

    public List<Diary> selectByAuthorId(long authorId);
}
