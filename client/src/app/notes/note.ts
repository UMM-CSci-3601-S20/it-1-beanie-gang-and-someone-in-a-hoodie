export interface Note {
  _id: string;
  ownerID: string;
  body: string;
  addDate: string;
  expireDate: string;
  status: NoteStatus;

}

export type NoteStatus = 'active' | 'template' | 'draft' | 'deleted';
