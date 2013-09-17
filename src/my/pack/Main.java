package my.pack;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinUser.INPUT;
import com.sun.jna.win32.StdCallLibrary;

public class Main {

	public static void main(String[] args) {
		if (!System.getProperty("os.name").contains("Windows")) {
			System.err.println("ERROR: Windows only.");
			System.exit(1);
		}
		for(;;){
			try {
				Thread.sleep(1000*60*3);
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			System.out.println(moveMouse(getMousePosition()));
		}
	}

	private static int[] getMousePosition(){
		int[] coor = new int[2];
		User32.INSTANCE.GetCursorPos(coor);
		return coor;
	}

	private static String moveMouse(int[] coor){
		INPUT input = new INPUT();
		input.type = new DWORD(INPUT.INPUT_MOUSE);

		input.input.setType("mi");
		int[] pos = getMousePosition();
		input.input.mi.dx = new LONG((pos[0] * 65536)/User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
		input.input.mi.dy = new LONG((pos[1] * 65536)/User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
		input.input.mi.mouseData = new DWORD(0);
		input.input.mi.dwFlags = new DWORD(User32.MOUSEEVENTTF_MOVE | User32.MOUSEEVENTTF_VIRTUALDESK | User32.MOUSEEVENTTF_ABSOLUTE);
		input.input.mi.time = new DWORD(0);

		INPUT[] inArray = {input};

		int cbSize = input.size();
		DWORD nInputs = new DWORD(1);
		DWORD result = User32.INSTANCE.SendInput(nInputs, inArray, cbSize);
		return result.toString();
	}
	
	public interface User32 extends StdCallLibrary{
		public static final long MOUSEEVENTTF_MOVE = 0x0001L;
		public static final long MOUSEEVENTTF_VIRTUALDESK = 0x4000L;
		public static final long MOUSEEVENTTF_ABSOLUTE = 0x8000L;
		public static final int SM_CXSCREEN = 0x0;
		public static final int SM_CYSCREEN = 0x1;

		User32 INSTANCE = (User32) Native.loadLibrary("user32",User32.class);
		DWORD SendInput(DWORD dWord, INPUT[] input, int cbSize);

		int GetSystemMetrics(int Index);
		boolean GetCursorPos(int[] coor);
		public int SendMessageA(Pointer hwnd,int wMsg,int wParam, int lParam);
	}
}
