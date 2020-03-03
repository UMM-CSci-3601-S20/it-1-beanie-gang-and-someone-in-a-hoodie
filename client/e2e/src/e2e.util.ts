 export class E2EUtil {
   // from https://stackoverflow.com/questions/1349404/generate-random-string-characters-in-javascript
 public static randomText(length: number): string {
    let text = '';
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

    for (let i = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
  }

  public static randomBoolean(): boolean {
    return Boolean(Math.round(Math.random()));
  }
}
