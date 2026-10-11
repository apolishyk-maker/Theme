#!/usr/bin/env python3
"""Generate original black titanium AMOLED wallpapers and an Android icon pack.
All imagery is created locally during GitHub Actions build; no network permissions in the app.
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import math, xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
assets = ROOT / "app/src/main/assets"
icons_dir = ROOT / "app/src/main/res/drawable-nodpi"
xml_dir = ROOT / "app/src/main/res/xml"
for directory in (assets, icons_dir, xml_dir):
    directory.mkdir(parents=True, exist_ok=True)

W, H = 1440, 3120
def background(lock=False):
    im = Image.new("RGB", (W,H), (0,0,0))
    metal = Image.new("RGBA", (W,H), (0,0,0,0))
    d = ImageDraw.Draw(metal, "RGBA")
    # Brushed carbon weave is intentionally barely visible, especially near the clock.
    for y in range(1550,H-160,32):
        for x in range(0,W,32):
            if ((x//32)+(y//32))%2 == 0:
                d.line((x,y,x+17,y+15), fill=(145,153,164,11), width=2)
            else:
                d.line((x+17,y,x+32,y+14), fill=(110,120,135,8), width=2)
    # Titanium ribbons: arcs with subdued graphite->silver reflections.
    off = 120 if lock else 0
    for k in range(105):
        t = k/104
        a = int(10 + 48*(1-t))
        color=(140,147,159,a)
        bounds=(-1010+int(t*710)+off, 1170+int(t*170),
                1960-int(t*410)+off, 3750-int(t*270))
        d.arc(bounds, 190, 347, fill=color, width=4)
        if k%7==0:
            d.arc(tuple(q+5 for q in bounds), 193,345, fill=(212,216,223,18), width=2)
    for k in range(44):
        t=k/43
        a=int(25+34*(1-t))
        bounds=(-300+int(t*280)-off,1740+int(t*320),
                2000-int(t*90)-off,3550-int(t*70))
        d.arc(bounds, 194, 346, fill=(112,121,133,a), width=3)
    im=Image.alpha_composite(im.convert("RGBA"),metal).convert("RGB")
    return im
background(False).save(assets/"wallpaper_home.png", optimize=True)
background(True).save(assets/"wallpaper_lock.png", optimize=True)
Image.new("RGB",(W,H),(0,0,0)).save(assets/"wallpaper_amoled.png",optimize=True)

NAMES = {
    "phone":"PH", "messages":"MS", "camera":"CA", "gallery":"GL", "settings":"ST", "telegram":"TG",
    "browser":"WB", "music":"MU", "calendar":"CL", "files":"FL", "clock":"TM", "maps":"MP",
    "mail":"ML", "youtube":"YT", "whatsapp":"WA", "playstore":"PL", "notes":"NT",
    "contacts":"CT", "calculator":"01", "instagram":"IG", "store":"SH",
    "recorder":"RC", "weather":"WX", "games":"GM",
}
try:
    font=ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",140)
except OSError:
    font=ImageFont.load_default()
for name, abbreviation in NAMES.items():
    im=Image.new("RGBA",(432,432),(0,0,0,0))
    d=ImageDraw.Draw(im,"RGBA")
    for i in range(8):
        tone=43+i*4
        d.rounded_rectangle((9+i,9+i,423-i,423-i),radius=108,
          outline=(tone,tone+2,tone+7,125),width=2)
    d.rounded_rectangle((16,16,416,416),radius=100,fill=(13,15,18,255),
      outline=(153,157,166,230),width=4)
    for y in range(65,360,16):
        d.line((54,y,380,y),fill=(180,184,190,9),width=1)
    bx=d.textbbox((0,0),abbreviation,font=font)
    tw,th=bx[2]-bx[0],bx[3]-bx[1]
    d.text(((432-tw)/2,(432-th)/2-bx[1]),abbreviation,font=font,
        fill=(222,226,233,255),stroke_width=0)
    im.save(icons_dir/("icon_"+name+".png"),optimize=True)
(icons_dir/"app_icon.png").write_bytes((icons_dir/"icon_settings.png").read_bytes())

# Common Samsung/Google/third-party activities. Unmatched apps keep original icons.
components = {
    "phone":["com.samsung.android.dialer/.DialtactsActivity","com.android.dialer/.main.impl.MainActivity"],
    "messages":["com.samsung.android.messaging/.ui.ConversationList","com.google.android.apps.messaging/.ui.ConversationListActivity","com.google.android.apps.messaging/.main.MainActivity"],
    "camera":["com.sec.android.app.camera/.Camera"],
    "gallery":["com.sec.android.gallery3d/.app.GalleryOpaqueActivity","com.google.android.apps.photos/.home.HomeActivity"],
    "settings":["com.android.settings/.Settings","com.android.settings/.homepage.SettingsHomepageActivity"],
    "telegram":["org.telegram.messenger/.DefaultIcon","org.telegram.messenger/.LaunchActivity"],
    "browser":["com.sec.android.app.sbrowser/.SBrowserMainActivity","com.android.chrome/com.google.android.apps.chrome.Main"],
    "music":["com.google.android.apps.youtube.music/.activities.MusicActivity","com.spotify.music/.MainActivity"],
    "calendar":["com.samsung.android.calendar/com.samsung.android.app.calendar.activity.MainActivity","com.google.android.calendar/com.android.calendar.AllInOneActivity"],
    "files":["com.sec.android.app.myfiles/.external.ui.MainActivity","com.sec.android.app.myfiles/.common.MainActivity"],
    "clock":["com.sec.android.app.clockpackage/.ClockPackage","com.google.android.deskclock/.DeskClock"],
    "maps":["com.google.android.apps.maps/com.google.android.maps.MapsActivity"],
    "mail":["com.google.android.gm/.ConversationListActivityGmail","com.samsung.android.email.provider/.ui.activity.WelcomeActivity"],
    "youtube":["com.google.android.youtube/com.google.android.youtube.HomeActivity"],
    "whatsapp":["com.whatsapp/.Main"],
    "playstore":["com.android.vending/.AssetBrowserActivity","com.android.vending/com.google.android.finsky.activities.MainActivity"],
    "notes":["com.samsung.android.app.notes/.NotesActivity"],
    "contacts":["com.samsung.android.contacts/.contacts.activities.PeopleActivity","com.google.android.contacts/.activities.PeopleActivity"],
    "calculator":["com.sec.android.app.popupcalculator/.Calculator"],
    "instagram":["com.instagram.android/.activity.MainTabActivity"],
    "store":["com.sec.android.app.samsungapps/.MainActivity"],
    "recorder":["com.sec.android.app.voicenote/.main.VoiceNoteActivity"],
    "weather":["com.sec.android.daemonapp/.app.MainActivity"],
    "games":["com.samsung.android.game.gamehome/.GameLauncherActivity"],
}
root=ET.Element("resources")
for n,comps in components.items():
    for component in comps:
        ET.SubElement(root,"item",component="ComponentInfo{"+component+"}",drawable="icon_"+n)
ET.ElementTree(root).write(assets/"appfilter.xml",encoding="utf-8",xml_declaration=True)
ET.ElementTree(root).write(xml_dir/"appfilter.xml",encoding="utf-8",xml_declaration=True)
root2=ET.Element("resources")
for n in NAMES:
    ET.SubElement(root2,"item",drawable="icon_"+n)
ET.ElementTree(root2).write(xml_dir/"drawable.xml",encoding="utf-8",xml_declaration=True)
print("Generated wallpapers, 24 icons and icon-pack mapping.")
