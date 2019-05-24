package moo.diarystorage.service;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import moo.diarystorage.db.DiaryTable.Row;
import pd.adt.Tag;
import pd.cprime.Cstring;
import pd.time.calendar.gregorian.DateBuilder;
import pd.time.calendar.gregorian.EasyTime;
import pd.util.Util;

public class Diary implements Serializable {

    private static final long serialVersionUID = 3695624023894234990L;

    public static Diary fromRow(Row row) {
        if (row == null) {
            return null;
        }
        Diary item = new Diary();
        item.id = row.id;
        item.title = row.title;
        item.content = row.content;
        item.userId = row.authorId;
        item.deserializeTags(row.tags);
        item.createTime = DateBuilder.toDate2(row.createTime);
        item.modifyTime = DateBuilder.toDate2(row.modifyTime);
        return item;
    }

    public long id;

    public String title;

    public String content;

    public long userId;

    public Set<Tag> tags = new HashSet<Tag>();

    public EasyTime createTime;

    public EasyTime modifyTime;

    void addTag(String... a) {
        for (String s : a) {
            Tag tag = Tag.fromString(s);
            tags.add(tag);
        }
    }

    public void addTag(Tag... a) {
        for (Tag tag : a) {
            tags.add(tag);
        }
    }

    private void deserializeTags(String s) {
        tags.clear();
        for (String tagString : s.split(",")) {
            tags.add(Tag.fromString(tagString));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            Diary o = (Diary) obj;
            return this.id == o.id
                    && Cstring.compare(o.title, this.title) == 0
                    && Cstring.compare(o.content, this.content) == 0
                    && this.userId == o.userId
                    && this.tags.equals(o.tags)
                    && this.createTime.equals(o.createTime)
                    && this.modifyTime.equals(o.modifyTime);
        }
        return false;
    }

    private String serializeTags() {
        return Util.toString(tags, ',');
    }

    public Row toRow() {
        Diary item = this;
        Row row = new Row();
        row.id = item.id;
        row.title = item.title;
        row.content = item.content;
        row.authorId = item.userId;
        row.tags = item.serializeTags();
        row.createTime = item.createTime == null ? null : item.createTime.toString();
        row.modifyTime = item.modifyTime == null ? null : item.modifyTime.toString();
        return row;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append('|')
                .append(title).append('|')
                .append(userId).append('|')
                .append(serializeTags()).append('|')
                .append(createTime).append('|')
                .append(modifyTime);
        return sb.toString();
    }
}
