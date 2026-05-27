import { useNavigate } from "react-router-dom";

function ArticleList({ boards, onDelete, onEdit }) {
  const nav = useNavigate();

  return (
    <div>
      <h3>게시글 목록</h3>
      <table style={{ width: '70%', textAlign: 'center', margin: '0 auto' }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>내용</th>
            <th>변경</th>
          </tr>
        </thead>
        <tbody>
          {boards.length > 0 ? (
            boards.map((board) => (
              <tr key={board.id}>
                <td>{board.id}</td>
                <td>{board.title}</td>
                {/* null일 경우 빈 문자열로 안전하게 처리 */}
                <td>{board.contents || ""}</td> 
                <td>
                  <button 
                    onClick={() => {
                      onEdit(board); 
                      nav("/write");
                    }}
                  >
                    수정
                  </button>
                  <button onClick={() => onDelete(board.id)}>삭제</button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="4">등록된 게시글이 없습니다.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default ArticleList;