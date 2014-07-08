library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity divider is
  generic(n   : natural := 25;
          top : natural := 24999999);
  port(clk_in  : in    std_logic;
       reset   : in    std_logic;
       clk_out : out std_logic);
end entity divider;

architecture behavioral of divider is
begin
end architecture behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity debouncer is
  port(input  : in  std_logic;
       clk    : in  std_logic;
       output : out std_logic := '0');
end entity debouncer;

architecture behavioral of debouncer is
begin
end architecture behavioral;

library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity add3 is
  port(input  : in  std_logic_vector(3 downto 0);
       output : out std_logic_vector(3 downto 0));
end entity add3;

architecture behavioral of add3 is
begin
end;

library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity bin2dec is
  port(input  : in  std_logic_vector(15 downto 0);
       output : out std_logic_vector(19 downto 0));
end entity bin2dec;

architecture behavioral of bin2dec is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.ALL;

entity display is
  port(input  : in  std_logic_vector(15 downto 0);
       clk    : in  std_logic;
       active : in  std_logic;
       seg    : out std_logic_vector(7 downto 0);
       an     : out std_logic_vector(3 downto 0));
end display;

architecture behavioral of display is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity switch is
  generic(init : std_logic);
  port(toggle : in  std_logic;
       output : out std_logic := init);
end switch;

architecture behavioral of switch is
begin
end;

library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity stopwatch is
  generic(n : natural := 8);
  port(input  : std_logic_vector(n - 1 downto 0);
       clk    : in  std_logic;
       rst    : in  std_logic;
       toggle : in  std_logic;
       dir    : in  std_logic;
       output : out std_logic_vector(n - 1 downto 0);
       active : out std_logic;
       ovf    : out std_logic);
end stopwatch;

architecture behavioral of stopwatch is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity sink4 is
  port(input : in std_logic_vector(3 downto 0));
end entity sink4;

architecture behavioral of sink4 is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity source4 is
  port(output : out std_logic_vector(3 downto 0));
end entity source4;

architecture behavioral of source4 is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity forktest0 is
end entity;

architecture behavioral of forktest0 is
  signal aaa : std_logic_vector(7 downto 0);
  signal bbb : std_logic_vector(7 downto 0);
  signal ccc : std_logic_vector(7 downto 0);
  signal ddd : std_logic_vector(7 downto 0);
begin
  src0 : entity work.source4 port map(aaa(3 downto 0));
  src1 : entity work.source4 port map(aaa(7 downto 4));
  dst0 : entity work.sink4 port map(aaa(3 downto 0));
  dst1 : entity work.sink4 port map(aaa(7 downto 4));

  src2 : entity work.source4 port map(bbb(3));
  src3 : entity work.source4 port map(bbb(7 downto 4));
  dst2 : entity work.sink4 port map(bbb(7 downto 0));

  src4 : entity work.source4 port map(ccc(7 downto 0));
  dst3 : entity work.sink4 port map(ccc(3 downto 0));
  dst4 : entity work.sink4 port map(ccc(7));

  src5 : entity work.source4 port map(ddd(3 downto 0));
  dst5 : entity work.sink4 port map(ddd(2 downto 0));
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity main is
  generic(seg_top : natural;
          seg_bot : natural);
  port(mclk : in  std_logic;
       btn  : in  std_logic_vector(3 downto 0);
       sw   : in  std_logic_vector(7 downto 0);
       led  : out std_logic_vector(7 downto 0) := (others => '0');
       seg  : out std_logic_vector(7 downto 0);
       an   : out std_logic_vector(3 downto 0));
end entity main;

architecture behavioral of main is
  signal dclk   : std_logic;
  signal disp   : std_logic_vector(19 downto 0);
  signal input  : std_logic_vector(15 downto 0);
  signal output : std_logic_vector(15 downto 0);
  signal toggle : std_logic;
  signal active : std_logic;
  constant COUNTER_WIDTH : integer := 16;
begin
  clock_1kHz : entity work.divider
  generic map(16, 24999)
  port map(mclk, '1', dclk);

  deb : entity work.debouncer
  port map(btn(0), mclk, toggle);

  watch : entity work.stopwatch
  generic map(COUNTER_WIDTH)
  port map(input, dclk, btn(3), toggle, sw(0), output, active, led(7));

  conv : entity work.bin2dec port map(input => output, output => disp);

  display : entity work.display
  port map(disp(19 downto 4), dclk, active, seg(seg_top downto seg_bot), an);

  led(6 downto 0)    <= (others => '0');
  input(15 downto 9) <= sw(7 downto 1);
  input(8 downto 0)  <= (others => '0');
end architecture behavioral;

