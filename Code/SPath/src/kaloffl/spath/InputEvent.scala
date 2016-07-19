package kaloffl.spath

trait InputEvent

case class MouseEvent(x: Int, y: Int, dragged: Boolean) extends InputEvent

case class KeyEvent(key: KeyEvent.Key, pressed: Boolean) extends InputEvent

object KeyEvent {
  case class Key(id: Int)

  val Key_A = Key(0)
  val Key_B = Key(1)
  val Key_C = Key(2)
  val Key_D = Key(3)
  val Key_E = Key(4)
  val Key_F = Key(5)
  val Key_G = Key(6)
  val Key_H = Key(7)
  val Key_I = Key(8)
  val Key_J = Key(9)
  val Key_K = Key(10)
  val Key_L = Key(11)
  val Key_M = Key(12)
  val Key_N = Key(13)
  val Key_O = Key(14)
  val Key_P = Key(15)
  val Key_Q = Key(16)
  val Key_R = Key(17)
  val Key_S = Key(18)
  val Key_T = Key(19)
  val Key_U = Key(20)
  val Key_V = Key(21)
  val Key_W = Key(22)
  val Key_X = Key(23)
  val Key_Y = Key(24)
  val Key_Z = Key(25)

  val Key_0 = Key(26)
  val Key_1 = Key(27)
  val Key_2 = Key(28)
  val Key_3 = Key(29)
  val Key_4 = Key(30)
  val Key_5 = Key(31)
  val Key_6 = Key(32)
  val Key_7 = Key(33)
  val Key_8 = Key(34)
  val Key_9 = Key(35)
  
  val Key_F1 = Key(36)
  val Key_F2 = Key(37)
  val Key_F3 = Key(38)
  val Key_F4 = Key(39)
  val Key_F5 = Key(40)
  val Key_F6 = Key(41)
  val Key_F7 = Key(42)
  val Key_F8 = Key(43)
  val Key_F9 = Key(44)
  val Key_F10 = Key(45)
  val Key_F11 = Key(46)
  val Key_F12 = Key(47)
  
  val Key_Tab = Key(48)
  val Key_Space = Key(49)
  val Key_Backspace = Key(50)
  val Key_Enter = Key(51)
  val Key_Shift = Key(52)
  val Key_Control = Key(53)
  val Key_Alt = Key(54)
  val Key_Escape = Key(55)
  
  val Key_Up = Key(56)
  val Key_Down = Key(57)
  val Key_Left = Key(58)
  val Key_Right = Key(59)
  
  val Mouse_1 = Key(60)
  val Mouse_2 = Key(61)
  val Mouse_3 = Key(62)
  val Mouse_4 = Key(63)
  val Mouse_5 = Key(64)
}