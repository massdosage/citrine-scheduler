//generic script to open a popup window, PageToOpen is relative to position of this
//javascript on the server, NOT the calling page's position
function showPopup(PageToOpen, WindowName, Width, Height)
{
   window.open(PageToOpen,WindowName,'toolbar=0,location=0,directories=0,status=0,hotkeys=0,menubar=0,scrollbars=yes,resizable=yes,width='+
               Width+',height='+Height);
   //return true;
}