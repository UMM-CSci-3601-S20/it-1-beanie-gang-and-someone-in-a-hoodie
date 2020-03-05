export interface Note {
  _id: string;
  owner: string;
  body: string;
  addDate: Date;
  expireDate: Date;
  status: NoteStatus;

}

export type NoteStatus = 'active' | 'template' | 'draft' | 'deleted';
