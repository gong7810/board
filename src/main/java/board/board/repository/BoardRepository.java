package board.board.repository;

import board.board.dto.BoardResDto;
import org.springframework.data.repository.CrudRepository;

public interface  BoardRepository extends CrudRepository<BoardResDto, Integer> {
}
