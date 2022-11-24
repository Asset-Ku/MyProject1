package com.example.demo.entity;

import com.example.demo.entity.util.MessageHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "author"})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "text")
    @NotBlank(message = "Поле 'текст' должно быть  заполнено!")
    @Length(max = 2048, message = "Сообщение не должен превышать 2048 символов!")
    private String text;

    @Column(name = "tag")
    @Length(max = 255, message = "Тэн не должен превышать 255 символов!")
    private String tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Users author;

    private String filename;

    @ManyToMany
    @JoinTable(
            name = "message_likes",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<Users> likes = new HashSet<>();

    public String getAuthorName() {
        return MessageHelper.getAuthorName(author);
    }
}
