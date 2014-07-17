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
  signal s_count : unsigned(n - 1 downto 0);
  signal s_clk   : std_logic;
begin
  clk_out <= s_clk;
  process(clk_in, reset)
  begin
    if reset = '0' then
      s_count <= (others => '0');
      s_clk <= '0';
    elsif clk_in'event and clk_in = '1' then
      if s_count = 0 then
        s_count <= to_unsigned(top, n);
        s_clk <= not s_clk;
      else
        s_count <= s_count - 1;
      end if;
    end if;
  end process;
end architecture behavioral;

-- UNIT
library ieee;
use ieee.std_logic_1164.all;

entity debouncer is
  port(input  : in  std_logic;
       clk    : in  std_logic;
       output : out std_logic := '0');
end entity debouncer;

architecture counter of debouncer is
  signal s_count : natural range 1 to 1000 := 1;
  signal s_value : std_logic               := '0';
begin
  output <= s_value;
  process(clk) is
  begin
    if rising_edge(clk) then
      if s_count = 1000 then
        s_value <= input;
        s_count <= 1;
      elsif s_value = input then
        s_count <= 1;
      else
        s_count <= s_count + 1;
      end if;
    end if;
  end process;
end counter;

architecture behavioral of debouncer is
  signal s_prev : std_logic := '0';
begin
  process(clk) is
  begin
    if clk'event and clk = '1' then
      if input = '1' and s_prev = '1' then
        output <= '1';
      elsif input = '0' and s_prev = '0' then
        output <= '0';
      else
        s_prev <= input;
      end if;
    end if;
  end process;
end architecture behavioral;

-- UNIT
library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity add3 is
  port(input  : in  std_logic_vector(3 downto 0);
       output : out std_logic_vector(3 downto 0));
end entity add3;

architecture behavioral of add3 is
begin
  output <= input when unsigned(input) < x"5"
            else std_logic_vector(unsigned(input) + to_unsigned(3, 4));
  end;

library ieee;
use ieee.std_logic_1164.ALL;
use ieee.numeric_std.all;

entity bin2dec is
  port(input  : in  std_logic_vector(15 downto 0);
       output : out std_logic_vector(19 downto 0));
end entity bin2dec;

architecture behavioral of bin2dec is
  type temp_array is array (16 downto 0) of std_logic_vector(36 downto 0);
  signal temp : temp_array;
begin
  temp(0)(15 downto 0)  <= input;
  temp(0)(36 downto 16) <= (others => '0');
  layer_for : for i in 0 to 15 generate
    add3_for : for j in 0 to 4 generate
      add3 : entity work.Add3 port map(temp(i)(15 + 4 * (j + 1) downto 16 + 4 * j),
      temp(i + 1)(16 + 4 * (j + 1) downto 17 + 4 * j));
    end generate add3_for;
    temp(i + 1)(16 downto 1) <= temp(i)(15 downto 0);
    temp(i + 1)(0)           <= '0';
  end generate layer_for;
  output <= temp(16)(35 downto 16);
end behavioral;

-- UNIT
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
  signal s_seg : unsigned(1 downto 0) := "00";
  type SEGMENT is array (3 downto 0) of std_logic_vector(3 downto 0);
  constant SEGMENTS : SEGMENT := ("0111", "1011", "1101", "1110");
  type DIGIT is array (9 downto 0) of std_logic_vector(6 downto 0);
  constant DIGITS : DIGIT := ("0010000", -- 9
  "0000000", -- 8
  "1111000", -- 7
  "0000010", -- 6
  "0010010", -- 5
  "0011001", -- 4
  "0110000", -- 3
  "0100100", -- 2
  "1111001", -- 1
  "1000000"); -- 0
begin
  process(clk)
  begin
    if clk'event and clk = '1' then
      s_seg         <= s_seg + "01";
      an              <= SEGMENTS(TO_INTEGER(s_seg));
      seg(6 downto 0) <= DIGITS(TO_INTEGER(
      unsigned(input(TO_INTEGER(s_seg) * 4 + 3 downto TO_INTEGER(s_seg) * 4))
    ));
    case s_seg is
      when "10"   => seg(7) <= '0';
      when "00"   => seg(7) <= not active;
      when others => seg(7) <= '1';
    end case;
  end if;
end process;
end behavioral;

-- UNIT
library ieee;
use ieee.std_logic_1164.all;

entity switch is
  generic(init : std_logic);
  port(toggle : in  std_logic;
       output : out std_logic := init);
end switch;

architecture behavioral of switch is
  signal s_output : std_logic := init;
begin
  process(toggle)
  begin
    if toggle'event and toggle = '1' then
      s_output <= not s_output;
      output   <= not s_output;
    end if;
  end process;
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
  signal s_counter : unsigned(n - 1 downto 0) := (others => '0');
  signal s_toggle  : std_logic;
  signal s_ref     : std_logic                := '0';
  signal s_ovf     : std_logic                := '0';
  constant one     : unsigned(n - 1 downto 0) := (0 => '1', others => '0');
begin
  switch : entity work.switch
  generic map('0')
  port map(toggle, s_toggle);
  process(clk)
  begin
    if clk'event and clk = '1' then
      if rst = '1' then
                -- Reset
        case dir is
          when '1'    => s_counter <= unsigned(input);
          when others => s_counter <= (others => '0');
        end case;
        s_ovf  <= '0';
        s_ref  <= s_toggle;
        active <= '0';
      elsif (s_ovf = '1') or (s_toggle = s_ref) then
                -- Inactive (or overflow)
        active <= '0';
      elsif dir = '1' and (s_counter > 0) then
                -- Tick decrease
        s_counter <= s_counter - one;
        active    <= '1';
      elsif dir = '0' and (s_counter < unsigned(input)) then
                -- Tick increase
        s_counter <= s_counter + one;
        active    <= '1';
      else
                -- We wanted to tick but couldn't due to the overflow
        s_ovf  <= '1';
        active <= '0';
      end if;
    end if;
  end process;
  output <= std_logic_vector(s_counter);
  ovf    <= s_ovf;
end behavioral;

-- UNIT
library ieee;
use ieee.std_logic_1164.all;

entity main is
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
begin
  clock_1kHz : entity work.divider generic map(16, 24999) port map(mclk, '1', dclk); -- 1 KHz

  deb : entity work.debouncer
  port map(btn(0), mclk, toggle);

  watch : entity work.stopwatch
  generic map(16)
  port map(input, dclk, btn(3), toggle, sw(0),
  output, active, led(7));

  conv : entity work.bin2dec port map(output, disp);

  display : entity work.display port map(disp(19 downto 4), dclk, active, seg, an);

  led(6 downto 0)    <= (others => '0');
  input(15 downto 9) <= sw(7 downto 1);
  input(8 downto 0)  <= (others => '0');
end architecture behavioral;
