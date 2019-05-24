package moo.diarystorage.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import moo.diarystorage.db.DiaryTable;
import moo.diarystorage.db.DiaryTable.Row;
import moo.diarystorage.service.Diary;
import moo.diarystorage.service.IDiaryStorageService;

public class DiaryStorageServiceImplementation implements IDiaryStorageService {

    @Override
    public void insertOrUpdate(Diary one) {
        DiaryTable.insertOrUpdate(one.toRow());
    }

    @Override
    public List<Diary> selectByAuthorId(long authorId) {
        List<DiaryTable.Row> rows = DiaryTable.selectByAuthorId(authorId, Integer.MAX_VALUE, 0);
        List<Diary> a = new ArrayList<>(rows.size());
        for (Row row : rows) {
            a.add(Diary.fromRow(row));
        }
        return a;
    }
}
