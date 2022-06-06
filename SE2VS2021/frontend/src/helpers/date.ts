export function fixDate(date: number): string {
  const x = new Date().getTimezoneOffset() * 60000;
  return new Date(date - x).toISOString().slice(0, -1);
}

export function timeFormatted(dateString: string): string {
  const time = new Date(dateString);
  const startHour = addZero(time.getHours());
  const startMinute = addZero(time.getMinutes());
  return `${startHour}:${startMinute}`;
}

export function dateFormatted(dateString: string): string {
  const date = new Date(dateString);
  const day = addZero(date.getDate());
  const month = addZero(date.getMonth() + 1);
  const year = date.getFullYear();
  return `${day}.${month}.${year}`;
}

function addZero(number: number): string | number {
  return number <= 9 ? "0" + number : number;
}
