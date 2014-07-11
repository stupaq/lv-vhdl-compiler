library ieee;
use ieee.std_logic_1164.all;

entity sink is
  generic(n   : in  integer := 4);
  port(input  : in  std_logic_vector(n downto 0));
end entity sink;

architecture behavioral of sink is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity source is
  generic(n   : in  integer := 4);
  port(output : out std_logic_vector(n downto 0));
end entity source;

architecture behavioral of source is
begin
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity branch_and_merge is
end entity;

architecture behavioral of branch_and_merge is
  signal aaa : std_logic_vector(7 downto 0);
  signal bbb : std_logic_vector(7 downto 0);
  signal ccc : std_logic_vector(7 downto 0);
  signal ddd : std_logic_vector(7 downto 0);
begin
  src0 : entity work.source port map(aaa(3 downto 0));
  src1 : entity work.source port map(aaa(7 downto 4));
  dst0 : entity work.sink port map(aaa(3 downto 0));
  dst1 : entity work.sink port map(aaa(7 downto 4));

  src2 : entity work.source port map(bbb(3 downto 0));
  src3 : entity work.source port map(bbb(7 downto 4));
  dst2 : entity work.sink generic map(7) port map(bbb);

  src4 : entity work.source generic map(7) port map(ccc);
  dst3 : entity work.sink port map(ccc(3 downto 0));
  dst4 : entity work.sink port map(ccc(7 downto 4));

  src5 : entity work.source generic map(7) port map(ddd);
  dst5 : entity work.sink generic map(7) port map(ddd);
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity many_ports is
  port(
    in0 : in std_logic;
    in1 : in std_logic;
    in2 : in std_logic;
    in3 : in std_logic;
    in4 : in std_logic;
    in5 : in std_logic;
    in6 : in std_logic;
    in7 : in std_logic;
    in8 : in std_logic;
    in9 : in std_logic;
    in10 : in std_logic;
    in11 : in std_logic;
    in12 : in std_logic;
    in13 : in std_logic;
    in14 : in std_logic;
    in15 : in std_logic;
    in16 : in std_logic;
    in17 : in std_logic;
    in18 : in std_logic;
    in19 : in std_logic;
    in20 : in std_logic;
    in21 : in std_logic;
    in22 : in std_logic;
    in23 : in std_logic;
    in24 : in std_logic;
    in25 : in std_logic;
    in26 : in std_logic;
    extra_in : in std_logic;
    extra_out : out std_logic);
end entity;

architecture behavioral of many_ports is
begin
  extra_out <= extra_in;
end behavioral;

library ieee;
use ieee.std_logic_1164.all;

entity many_ports_outer is
  port(
    input    : in  std_logic;
    in0   : in  std_logic;
    out0  : out std_logic);
end entity;

architecture behavioral of many_ports_outer is
begin
  many_ports : entity work.many_ports port map(in0, in0, in0, in0, in0, in0, in0, in0,
   in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0, in0,
    in0, in0, input, out0);
end behavioral;

entity process_outer is
  port(
    clk   : in  std_logic;
    in1   : in  std_logic;
    in2   : in  std_logic;
    out1  : out std_logic;
    out2  : out std_logic;
    out3  : out std_logic
    );
end entity;

architecture behavioral of process_outer is
begin
  process (clk, rst) is
  begin
    if clk'event and clk = '1' then
      if in1 = '1' then
        -- Missing else branch.
        out1 <= in2;
      end if;
      if in1 = '1' then
        out2 <= in2;
      else
        -- Assigned in every branch.
        out2 <= not in2;
        -- Assigned in one branch (out of two possible).
        out3 <= in2;
      end if;
      -- Assignment in every branch, but depends on previous value.
      out4 <= not out4;
      -- Assignment in every branch and combination of other signals.
      out5 <= out4 and in2;
    end if;
  end process;
  rst <= '1';
end behavioral;

-- FIXME needs total cleanup
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
       output : out std_logic_vector(n - 1 downto 0);
       active : out std_logic;
       toggle : in  std_logic;
       dir    : in  std_logic;
       ovf    : out std_logic);
end stopwatch;

architecture behavioral of stopwatch is
begin
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
  port map(input, dclk, btn(3), output, active, led(7));

  conv : entity work.bin2dec port map(input => output, output => disp);

  display : entity work.display
  port map(disp(19 downto 4), dclk, active, seg(seg_top downto seg_bot), an);

  led(6 downto 0)    <= (others => '0');
  input(15 downto 9) <= sw(7 downto 1);
  input(8 downto 0)  <= (others => '0');
end architecture behavioral;

