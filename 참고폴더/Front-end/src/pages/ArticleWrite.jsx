
function ArticleWrite({ inputs, onChange, onSubmit, editingId, setEditingId, setInputs }) {
  const { title, contents } = inputs;

  return (
    <div >
      <h3>{editingId ? "게시글 수정" : "새 게시글 작성"}</h3>
      <input name="title" value={title} onChange={onChange} placeholder="제목" /> <br/>
      <textarea name="contents" value={contents} onChange={onChange} placeholder="내용" /><br/>
      <button onClick={onSubmit}>{editingId ? "수정 완료" : "등록하기"}</button>
      {editingId && (
        <button onClick={() => { setEditingId(null); setInputs({ title: '', contents: '' }); }}>취소</button>
      )}
    </div>
  );
}

export default ArticleWrite;